package lang

enum class TokenType {
    // słowa kluczowe
    NIECH, KURTYNA, SIĘ, PODNIESIE, OPADNIE, // rozpoczecie programu i koniec

    OŚWIADCZAM, POWOŁANIE, DO, ŻYCIA, BYTU, BYT, O, IMIENIU, // deklaracja zmiennej
    KTÓRY, OD, PIERWSZEGO, TCHNIENIA, NIESIE, WARTOŚĆ, // przypisanie wartosci
    WARTOŚCIĄ, //

    ZOSTANIE, OBDARZONY,
    PRZYJMIE, DAR, Z, UST, UŻYTKOWNIKA,

    WYBACZCIE, ŚMIAŁOŚĆ, LECZ, ROZWAŻAM, CZY, PRZYPADKIEM,
    JEŻELI, TO, ZAISTE, UCZYNIĘ, CO, INNEGO,
    JEŚLI, LOS, ZECHCE,
    DOPÓKI, POZWALA,

    OZNAJMIJ, // println(x)

    // operatory słowne
    JEST, NIE_JEST, PRZEWYŻSZA, USTĘPUJE, NIE_USTĘPUJE, NIE_PRZEWYŻSZA,
    ORAZ, ALBO, NIE,

    // wartości
    IDENTIFIER, NUMBER, STRING,

    // symbole
    PLUS, MINUS, STAR, SLASH,
    LEFT_PAREN, RIGHT_PAREN,
    LEFT_BRACE, RIGHT_BRACE,
    SEMICOLON,

    EOF // end of file
}