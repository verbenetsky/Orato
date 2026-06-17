package lang

// analiza leksykalna, czyli tworzenie tokenow
class Lexer(private val source: String) {
    private val tokens = mutableListOf<Token>()
    private var start = 0 // odpowiada za poczatek aktualnie rozpoznawanego tokenu
    private var nextCharIndex = 0 // indeks następnego znaku do przeczytania
    private var line = 1 // aktualna linijka

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            start = nextCharIndex
            scanToken()
        }

        tokens.add(Token(TokenType.EOF, "", line))
        return tokens
    }

    private fun scanToken() {
        val c = advance() // przechodzi po znakach

        when {
            c == '(' -> addToken("(", TokenType.LEFT_PAREN)
            c == ')' -> addToken(")", TokenType.RIGHT_PAREN)
            c == '{' -> addToken("{", TokenType.LEFT_BRACE)
            c == '}' -> addToken("}", TokenType.RIGHT_BRACE)
            c == ';' -> addToken(";", TokenType.SEMICOLON)

            c == '+' -> addToken("+", TokenType.PLUS)
            c == '-' -> addToken("-", TokenType.MINUS)
            c == '*' -> addToken("*", TokenType.STAR)

            c == '/' -> {
                when {
                    match('/') -> skipLineComment()
                    match('*') -> skipBlockComment()
                    else -> addToken("/", TokenType.SLASH) // dzielenie
                }
            }

            c == ' ' || c == '\r' || c == '\t' -> {
                // ignorujemy biale znaki
            }

            c == '\n' -> {
                line++
            }

            c == '"' -> string() // jesli wczytany znak jest cudzyslowem "

            c.isDigit() -> number()

            isIdentifierStart(c) -> identifierOrKeyword()

            else -> {
                throw RuntimeException(
                    "Błąd leksykalny, linia $line:\n" +
                            "'$c', nikt nie rozpoznaje."
                )
            }
        }
    }

    private fun isAtEnd(): Boolean {
        return nextCharIndex >= source.length
    }

    private fun advance(): Char {
        val currentChar = source[nextCharIndex]
        nextCharIndex++
        return currentChar
    }

    // sprawdza co jest po
    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[nextCharIndex] != expected) return false // sprawdzamy następny nieprzeczytany znak

        nextCharIndex++
        return true
    }

    // zwraca znak na ktorym aktualnie znajduje sie lexer
    private fun peek(): Char {
        if (isAtEnd()) return '\u0000' // sztuczny znak koncaf
        return source[nextCharIndex]
    }

    private fun peekNext(): Char {
        if (nextCharIndex + 1 >= source.length) return '\u0000' // null character
        return source[nextCharIndex + 1]
    }

    private fun skipLineComment() {
        while (!isAtEnd() && peek() != '\n') {
            advance()
        }
    }

    private fun skipBlockComment() {
        while (!isAtEnd()) {
            if (peek() == '\n') {
                line++
            }

            if (peek() == '*' && peekNext() == '/') {
                advance() // zjada '*'
                advance() // zjada '/'
                return
            }

            advance()
        }

        throw RuntimeException(
            "Błąd leksykalny, linia $line:\n" +
                    "Komentarz wielolinijkowy rozpoczął się, ale nigdy nie został zakończony."
        )
    }

    private fun number() {
        while (!isAtEnd()) {
            val c = peek()

            if (c.isDigit()) {
                advance()
            } else {
                break
            }
        }

        val number = source.substring(start, nextCharIndex)

        addToken(number, TokenType.NUMBER)
    }

    private fun string() {
        while (!isAtEnd() && peek() != '"') {
            if (peek() == '\n') {
                line++
            }

            advance()
        }

        if (isAtEnd()) {
            throw RuntimeException(
                "Błąd leksykalny, linia $line:\n" +
                        "Tekst rozpoczął się cudzysłowem, lecz nigdy nie został domknięty."
            )
        }

        advance() // zjada zamykający cudzysłów

        val value = source.substring(start + 1, nextCharIndex - 1) // +1 i -1 zrobione dla tego zeby nie brac pod uwage
        // otwierajacych i zamykajacych cudzyslowow
        addToken(value, TokenType.STRING)
    }

    private fun addToken(value: String, tokenType: TokenType) {
        tokens.add(
            Token(
                type = tokenType,
                value = value,
                line = line
            )
        )
    }

    // zmienna nie moze sie zaczynac od cyfry ale moze od _ i od litery
    private fun isIdentifierStart(c: Char): Boolean {
        return c == '_' || c.isLetter()
    }

    // czyta cale slowo i sprawdza czy to slowo kluczowe czy nazwa zmiennej
    private fun identifierOrKeyword() {
        while (!isAtEnd() && isIdentifierPart(peek())) {
            advance()
        }

        val value = source.substring(start, nextCharIndex)
        val lowered = value.lowercase() // jezyk nie rozroznia male i duze litery

        // jesli sie udalo dopasowac jakies slowo to jest slowo kluczowe a jesli nie to nazwa zmiennej
        val type = keywords[lowered] ?: TokenType.IDENTIFIER

        addToken(value, type)
    }

    // sprawdzanie czy kolejne znaki moga byc czescia slowa kluczowego lub zmiennej
    private fun isIdentifierPart(c: Char): Boolean {
        return if (c == '_' || c.isLetterOrDigit()) {
            true
        } else {
            false
        }
    }
}