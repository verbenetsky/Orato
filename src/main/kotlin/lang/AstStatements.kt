package lang

sealed interface Expr { // czyli cos co ma wartosc: 5, "lol", a + 1, nie x itd
    val line: Int
}

data class NumberExpr(
    val value: Int,
    override val line: Int
) : Expr

data class StringExpr(
    val value: String,
    override val line: Int
) : Expr

data class VariableExpr(
    val name: String,
    override val line: Int
) : Expr

data class BinaryExpr(
    val left: Expr,
    val operator: Token,
    val right: Expr,
    override val line: Int
) : Expr

sealed interface Stmt { // instrukcje, to co program robi, wykonuje akcje
    val line: Int
}

data class VarDeclaration(
    val name: String,
    val initializer: Expr?,
    override val line: Int
) : Stmt

data class Assignment(
    val name: String,
    val value: Expr,
    override val line: Int
) : Stmt

data class PrintStmt(
    val expr: Expr,
    override val line: Int
) : Stmt

data class IfStmt(
    val condition: Expr, // warunek glowny
    val thenBranch: List<Stmt>,
    val elseIfBranches: List<ElseIfBranch>,
    val elseBranch: List<Stmt>?,
    override val line: Int
) : Stmt

data class ElseIfBranch(
    val condition: Expr, // warunek else if
    val body: List<Stmt>
)

data class InputStmt(
    val name: String,
    override val line: Int
) : Stmt

data class WhileStmt(
    val condition: Expr,
    val body: List<Stmt>,
    override val line: Int
) : Stmt

// jest dla operatora ktory ma tylko jedna strone, czyli dla "nie" (NOT)
data class UnaryExpr(
    val operator: Token,
    val right: Expr,
    override val line: Int
) : Expr