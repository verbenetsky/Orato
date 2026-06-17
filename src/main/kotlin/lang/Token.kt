package lang

data class Token(
    val type: TokenType,
    val value: String,
    val line: Int
)
