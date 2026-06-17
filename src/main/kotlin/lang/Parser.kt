package lang

// parser dostaje liste tokenow z lexara List<Token> i zamienia na List<Statement>, czyli na drzewo AST

class Parser(private val tokens: List<Token>) {
    private var current = 0

    fun parse(): List<Stmt> {
        return program()
    }

    private fun program(): List<Stmt> {
        val statements = mutableListOf<Stmt>()

        consume(TokenType.NIECH, "Program powinien rozpocząć się słowem 'Niech'.")
        consume(TokenType.KURTYNA, "Oczekiwano słowa 'kurtyna'.")
        consume(TokenType.SIĘ, "Oczekiwano słowa 'się'.")
        consume(TokenType.PODNIESIE, "Oczekiwano słowa 'podniesie'.")

        while (!isAtEnd() && !isProgramEnd()) {
            statements.add(statement())
        }

        consume(TokenType.NIECH, "Program powinien zakończyć się frazą 'Niech kurtyna opadnie'.")
        consume(TokenType.KURTYNA, "Oczekiwano słowa 'kurtyna'.")
        consume(TokenType.OPADNIE, "Oczekiwano słowa 'opadnie'.")

        consume(TokenType.EOF, "Po zakończeniu programu nie powinno być dodatkowego kodu.")

        return statements
    }

    private fun statement(): Stmt {
        return when {
            check(TokenType.OŚWIADCZAM) -> declarationAndAssignmentVariable()
            check(TokenType.NIECH) -> niechStatement()
            check(TokenType.OZNAJMIJ) -> printStatement()
            check(TokenType.WYBACZCIE) -> ifStatement()
            check(TokenType.DOPÓKI) -> whileLoop()

            else -> {
                throw RuntimeException(
                    "Błąd składniowy, linia ${peek().line}:\n" +
                            "Słowa nie ułożyły się w żadną znaną instrukcję tego języka."
                )
            }
        }
    }

    private fun printStatement(): Stmt {
        val startToken = consume(TokenType.OZNAJMIJ, "Oczekiwano słowa 'oznajmij'. ")

        consume(TokenType.LEFT_PAREN, "Po słowie 'oznajmij' oczekiwano znaku '('.")

        val expr = expression()

        consume(TokenType.RIGHT_PAREN, "Po wartości do wypisania oczekiwano znaku ')'.")

        consume(
            TokenType.SEMICOLON,
            "Oczekiwano średnika ';' po instrukcji wypisywania."
        )

        return PrintStmt(
            expr = expr,
            line = startToken.line
        )
    }

    private fun whileLoop(): Stmt {
        val startToken = consume(TokenType.DOPÓKI, "Oczekiwano słowa 'Dopóki'.")
        consume(TokenType.LOS, "Oczekiwano słowa 'los'.")
        consume(TokenType.POZWALA, "Oczekiwano słowa 'pozwala'.")

        consume(TokenType.LEFT_PAREN, "Po frazie 'Dopóki los pozwala' oczekiwano '('.")

        val condition = expression()

        consume(TokenType.RIGHT_PAREN, "Po warunku pętli oczekiwano ')'.")

        val body = block()

        return WhileStmt(
            condition = condition,
            body = body,
            line = startToken.line
        )
    }

    private fun ifStatement(): Stmt {
        val listOfElseIfStmt = mutableListOf<ElseIfBranch>()

        val startToken = consume(
            TokenType.WYBACZCIE,
            "Oczekiwano słowa 'Wybaczcie' rozpoczynającego instrukcję warunkową."
        )

        consume(TokenType.ŚMIAŁOŚĆ, "Oczekiwano słowa 'śmiałość'.")
        consume(TokenType.LECZ, "Oczekiwano słowa 'lecz'.")
        consume(TokenType.ROZWAŻAM, "Oczekiwano słowa 'rozważam'.")
        consume(TokenType.CZY, "Oczekiwano słowa 'czy'.")
        consume(TokenType.PRZYPADKIEM, "Oczekiwano słowa 'przypadkiem'.")

        consume(
            TokenType.LEFT_PAREN,
            "Po frazie 'Wybaczcie śmiałość, lecz rozważam, czy przypadkiem' oczekiwano '('."
        )

        val ifCondition = expression()

        consume(
            TokenType.RIGHT_PAREN,
            "Po warunku instrukcji if oczekiwano ')'."
        )

        val ifBlock = block()

        while (check(TokenType.LECZ) && checkNext(TokenType.JEŚLI)) {
            consume(TokenType.LECZ, "Oczekiwano słowa 'lecz'.")
            consume(TokenType.JEŚLI, "Oczekiwano słowa 'jeśli'.")
            consume(TokenType.LOS, "Oczekiwano słowa 'los'.")
            consume(TokenType.ZECHCE, "Oczekiwano słowa 'zechce'.")

            consume(
                TokenType.LEFT_PAREN,
                "Po frazie 'lecz jeśli los zechce' oczekiwano '('."
            )

            val condition = expression()

            consume(
                TokenType.RIGHT_PAREN,
                "Po warunku else-if oczekiwano ')'."
            )

            val elseIfBlock = block()

            listOfElseIfStmt.add(
                ElseIfBranch(
                    condition = condition,
                    body = elseIfBlock
                )
            )
        }

        var elseBlock: List<Stmt>? = null

        if (match(TokenType.JEŻELI)) {
            consume(TokenType.NIE, "Oczekiwano słowa 'nie'.")
            consume(TokenType.TO, "Oczekiwano słowa 'to'.")
            consume(TokenType.ZAISTE, "Oczekiwano słowa 'zaiste'.")
            consume(TokenType.UCZYNIĘ, "Oczekiwano słowa 'uczynię'.")
            consume(TokenType.CO, "Oczekiwano słowa 'co'.")
            consume(TokenType.INNEGO, "Oczekiwano słowa 'innego'.")

            elseBlock = block()
        }

        return IfStmt(
            condition = ifCondition,
            thenBranch = ifBlock,
            elseIfBranches = listOfElseIfStmt,
            elseBranch = elseBlock,
            line = startToken.line
        )
    }

    private fun expression(): Expr {
        return logic()
    }

    //  && i || maja taki sam priorytet, w kotlinie domyslnie && ma wiekszy priorytet
    private fun logic(): Expr {
        var expr = comparison()

        while (match(TokenType.ORAZ, TokenType.ALBO)) {
            val operator = previous()
            val right = comparison()

            expr = BinaryExpr(
                left = expr,
                operator = operator,
                right = right,
                line = operator.line
            )
        }

        return expr
    }

    // tworzy albo zwykly jedno elementowy expression, czyli x lub 10 lub "cos" lub binary expression
    // x ustepuja 10 i td
    private fun comparison(): Expr {
        var expr = term()
        // pierwsza (lewa) czesc expression, czyli jesli jest samo x lub 10 lub "cos" to tak i zostanie
        // jesli jescze cos jest to bedzie to binary expression z operatorem i prawa czescia

        if (match(
                TokenType.JEST,
                TokenType.NIE_JEST,
                TokenType.PRZEWYŻSZA,
                TokenType.USTĘPUJE,
                TokenType.NIE_USTĘPUJE,
                TokenType.NIE_PRZEWYŻSZA
            )
        ) {
            val operator = previous()
            val right = term() // prawa czesc binary expression

            expr = BinaryExpr(
                left = expr,
                operator = operator,
                right = right,
                line = operator.line
            )
        }
        return expr
    }

    private fun term(): Expr {
        var expr = factor()
        while (match(TokenType.MINUS, TokenType.PLUS)) {
            val operator = previous() // bo match zjadlo operator i jest na nastepnej pozycji
            val right = factor()

            expr = BinaryExpr(
                left = expr,
                operator = operator,
                right = right,
                line = operator.line
            )
        }
        return expr
    }

    private fun factor(): Expr {
        var expr = unary()
        while (match(TokenType.STAR, TokenType.SLASH)) {
            val operator = previous()
            val right = unary()

            expr = BinaryExpr(
                left = expr,
                operator = operator,
                right = right,
                line = operator.line
            )
        }
        return expr
    }

    private fun unary(): Expr {
        if (match(TokenType.NIE)) {
            val operator = previous()
            val right = unary()

            return UnaryExpr(
                operator = operator,
                right = right,
                line = operator.line
            )
        }
        return primary()
    }

    // tworzy jedno z expression, czyli
    // x albo 10 albo "123" - tylko wartosci takiego typu moze przejmowac zmienna, czyli string lub number lub jakas zmienna
    // lub logiczny jakis statement, np x jest 10, 30 nie przewyzsza 10 i td
    private fun primary(): Expr {
        if (match(TokenType.STRING)) {
            val token = previous()

            return StringExpr(
                value = token.value,
                line = token.line
            )
        }

        if (match(TokenType.IDENTIFIER)) {
            val token = previous()

            return VariableExpr(
                name = token.value,
                line = token.line
            )
        }

        if (match(TokenType.NUMBER)) {
            val token = previous()

            return NumberExpr(
                value = token.value.toInt(),
                line = token.line
            )
        }

        // jesli to odkomentowac to mozna bedzie robic takie cos:
        //Oświadczam powołanie do życia bytu o imieniu b który od pierwszego tchnienia niesie wartość (b jest 10);
        // a potem dac:
        // oznajmij(b)
        // to wynikiem bedzie bool, np false
        // ale nie ma obslugi przypadku zeby odrazu przypisac jakis bool do zmiennej

//        if (match(TokenType.LEFT_PAREN)) {
//            val expr = expression()
//
//            consume(
//                TokenType.RIGHT_PAREN,
//                "Po wyrażeniu w nawiasie oczekiwano ')'."
//            )
//            return expr
//        }

        if (match(TokenType.LEFT_PAREN)) {
            val expr = expression()

            consume(
                TokenType.RIGHT_PAREN,
                "Po wyrażeniu w nawiasie oczekiwano ')'."
            )
            return expr
        }

        throw RuntimeException(
            "Błąd składniowy, linia ${peek().line}:\n" +
                    "Oczekiwano liczby, tekstu albo nazwy bytu."
        )
    }



    // sprawdza czy type nalezy do jakiegos typu tokena
    private fun check(type: TokenType): Boolean {
        val currentToken = peek()
        return currentToken.type == type
    }

    // sprawdza czy aktualny token to token konca pliku
    private fun isAtEnd(): Boolean {
        val currentToken = peek()
        return currentToken.type == TokenType.EOF
    }

    // zwraca token na ktorym aktualnie znajduje sie parser
    private fun peek(): Token {
        return tokens[current]
    }

    // stworzenie zmiennej i przypisanie do niej wartosci
    private fun declarationAndAssignmentVariable(): Stmt {
        val startToken = consume(TokenType.OŚWIADCZAM, "Oczekiwano słowa 'Oświadczam'.")
        consume(TokenType.POWOŁANIE, "Oczekiwano słowa 'powołanie'.")
        consume(TokenType.DO, "Oczekiwano słowa 'do'.")
        consume(TokenType.ŻYCIA, "Oczekiwano słowa 'życia'.")
        consume(TokenType.BYTU, "Oczekiwano słowa 'bytu'.")
        consume(TokenType.O, "Oczekiwano słowa 'o'.")
        consume(TokenType.IMIENIU, "Oczekiwano słowa 'imieniu'.")

        val name = consume(
            TokenType.IDENTIFIER,
            "Po słowach 'o imieniu' oczekiwano nazwy bytu."
        )

        var initializer: Expr? = null

        if (match(TokenType.KTÓRY)) {
            consume(TokenType.OD, "Oczekiwano słowa 'od'.")
            consume(TokenType.PIERWSZEGO, "Oczekiwano słowa 'pierwszego'.")
            consume(TokenType.TCHNIENIA, "Oczekiwano słowa 'tchnienia'.")
            consume(TokenType.NIESIE, "Oczekiwano słowa 'niesie'.")
            consume(TokenType.WARTOŚĆ, "Oczekiwano słowa 'wartość'.")

            initializer = expression()
        }

        consume(TokenType.SEMICOLON, "Oczekiwano średnika ';' na końcu deklaracji.")

        return VarDeclaration(
            name = name.value,
            initializer = initializer,
            line = startToken.line
        )
    }

    // od Niech sie zaczynaja trzy rzeczy:
    // 1. Niech byt x zostanie obdarzony wartością XYZ - przypisanie wartosci do istniejacej zmiennej
    // 2. Niech kurtyna opadnie - na zakonczenie programu
    // 3. Niech byt x przyjmie dar z ust użytkownika - na wczytanie zmiennej, czyli read(x)
    private fun niechStatement(): Stmt {
        val startToken = consume(TokenType.NIECH, "Oczekiwano słowa 'Niech'.")

        if (match(TokenType.BYT)) {
            val name = consume(
                TokenType.IDENTIFIER,
                "Po słowie 'byt' oczekiwano nazwy bytu."
            )

            if (match(TokenType.ZOSTANIE)) {
                consume(TokenType.OBDARZONY, "Oczekiwano słowa 'obdarzony'.")
                consume(TokenType.WARTOŚCIĄ, "Oczekiwano słowa 'wartością'.")

                val value = expression()

                consume(TokenType.SEMICOLON, "Oczekiwano średnika ';' po przypisaniu wartości.")

                return Assignment(
                    name = name.value,
                    value = value,
                    line = startToken.line
                )
            }

            if (match(TokenType.PRZYJMIE)) {
                consume(TokenType.DAR, "Oczekiwano słowa 'dar'.")
                consume(TokenType.Z, "Oczekiwano słowa 'z'.")
                consume(TokenType.UST, "Oczekiwano słowa 'ust'.")
                consume(TokenType.UŻYTKOWNIKA, "Oczekiwano słowa 'użytkownika'.")

                consume(TokenType.SEMICOLON, "Oczekiwano średnika ';' po instrukcji wczytywania.")

                return InputStmt(
                    name = name.value,
                    line = startToken.line
                )
            }

            throw RuntimeException(
                "Błąd składniowy, linia ${peek().line}:\n" +
                        "Po nazwie bytu oczekiwano słowa 'zostanie' albo 'przyjmie'."
            )
        }

        throw RuntimeException(
            "Błąd składniowy, linia ${peek().line}:\n" +
                    "Po słowie 'Niech' oczekiwano słowa 'byt'."
        )
    }

    // zasieg if, else if, else, while za pomoca: { }
    private fun block(): List<Stmt> {
        consume(TokenType.LEFT_BRACE, "Oczekiwano '{' rozpoczynającego blok.")

        val statements = mutableListOf<Stmt>()

        while (!check(TokenType.RIGHT_BRACE)) {
            statements.add(statement())
        }

        consume(TokenType.RIGHT_BRACE, "Oczekiwano '}' rozpoczynającego blok.")

        return statements
    }

    // sprawdza czy type nalezy do jakiegos typu tokena jesli tak to przechodzi dalej po liscie tokenow
    private fun consume(type: TokenType, message: String): Token {
        if (check(type)) {
            return advance()
        }

        throw RuntimeException(
            "Błąd składniowy, linia ${peek().line}:\n$message"
        )
    }

    // przechodzi dalej po liscie tokenow
    private fun advance(): Token {
        if (!isAtEnd()) {
            current++
        }
        return previous()
    }

    // sprawdza jaki token sie znajduje na poprzedniej pozycji w liscie tokenow
    private fun previous(): Token {
        return tokens[current - 1]
    }

    // czy aktualny token jest takiego typu ktorego szukam ktorego szukam
    // lub czy jakis z tokenow nalezy
    // jesli tak to zjadamy token
    private fun match(vararg types: TokenType): Boolean {
        for (type in types) {
            if (check(type)) {
                advance()
                return true
            }
        }
        return false
    }

    // sprawdzamy czy nastepny token jest jakiego okreslonego typu
    private fun checkNext(type: TokenType): Boolean {
        if (current + 1 >= tokens.size) return false
        return tokens[current + 1].type == type
    }

    private fun isProgramEnd(): Boolean {
        return check(TokenType.NIECH) && checkNext(TokenType.KURTYNA)
    }


    //  expression()
    //  └── logic()         oraz, albo
    //      └── comparison()  jest, nie_jest, przewyższa, ustępuje...
    //          └── term()        +, -
    //              └── factor()      *, /
    //                  └── unary()       nie
    //                      └── primary()     liczba, string, zmienna, nawiasy

    // Czyli priorytet od najsilniejszego do najslabszego:
    // 1. primary()      -> 10, "tekst", x, ( ... )
    // 2. unary()        -> nie x
    // 3. factor()       -> * /
    // 4. term()         -> + -
    // 5. comparison()   -> jest, nie_jest, przewyższa, ustępuje...
    // 6. logic()        -> oraz, albo
}