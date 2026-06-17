package lang

class Interpreter {
    // key, value -> np. x = 10, x = null
    private val variables = mutableMapOf<String, Any?>()

    fun interpret(statements: List<Stmt>) {
        for (statement in statements) {
            execute(statement)
        }
    }

    private fun execute(stmt: Stmt) {
        when (stmt) {
            is Assignment -> {
                executeAssignment(stmt)
            }

            is IfStmt -> {
                executeIfStmt(stmt)
            }

            is InputStmt -> {
                executeInputStmt(stmt)
            }

            is PrintStmt -> {
                executePrintStmt(stmt)
            }

            is VarDeclaration -> {
                executeVarDeclaration(stmt)
            }

            is WhileStmt -> {
                executeWhileStmt(stmt)
            }
        }
    }

    private fun executeWhileStmt(stmt: WhileStmt) {

        while(true) {
            val conditionValue = evaluate(stmt.condition)

            if (conditionValue !is Boolean) {
                throw RuntimeException(
                    "Błąd typu, linia ${stmt.line}:\n" +
                            "Warunek pętli while musi mieć wartość logiczną."
                )
            }

            // jesli warunek while nie jest prawda to wychodzimy z petli while
            if (!conditionValue) {
                break
            }
            executeBlock(stmt.body)
        }
    }

    private fun executeVarDeclaration(stmt: VarDeclaration) {
        if (variables.containsKey(stmt.name)) {
            throw RuntimeException(
                "Błąd semantyczny, linia ${stmt.line}:\n" +
                        "Byt '${stmt.name}' został już wcześniej powołany do życia."
            )
        }


        val value = if (stmt.initializer != null) {
            evaluate(stmt.initializer)
        } else { // domyslnie wartosc jest null jest jest stworzona ale zadna wartosc nie przypisana
            null
        }

        variables[stmt.name] = value
    }

    // oznajmij(x);
    private fun executePrintStmt(stmt: PrintStmt) {
        val value = evaluate(stmt.expr)
        println(value)
    }

    private fun executeInputStmt(stmt: InputStmt) {
        if (!variables.containsKey(stmt.name)) {
            throw RuntimeException(
                "Błąd semantyczny, linia ${stmt.line}:\n" +
                        "Byt '${stmt.name}' nie został wcześniej powołany do życia."
            )
        }

        val inputValueString = readln()


        val potentialIntVariable = inputValueString.toIntOrNull()

        if (potentialIntVariable == null) { // nie udalo sie sparsowac na int, czyli to jest String
            variables[stmt.name] = inputValueString
        } else {
            variables[stmt.name] = potentialIntVariable
        }

    }

    private fun executeIfStmt(stmt: IfStmt) {

        val conditionValue = evaluate(stmt.condition)

        if (conditionValue !is Boolean) { // condition value musi byc typu boolean
            throw RuntimeException(
                "Błąd typu, linia ${stmt.line}:\n" +
                        "Warunek instrukcji warunkowej musi mieć wartość logiczną."
            )
        }

        if (conditionValue) { // jesli warunek z condition jest prawdziwy to prawdziwy jest caly if (...)
            executeBlock(stmt.thenBranch)
            return
        }

        // przechodzenie po wszystkich else if
        for (s in stmt.elseIfBranches) {

            val conditionValue = evaluate(s.condition)

            if (conditionValue !is Boolean) {
                throw RuntimeException(
                    "Błąd typu, linia ${stmt.line}:\n" +
                            "Warunek gałęzi else-if musi mieć wartość logiczną."
                )
            }

            // jesli jest prawdziwy warunek z if else ktory obecnie sprawdzany jest
            if (conditionValue) {
                executeBlock(s.body)
                return
            }
        }

        // jesli nie ma elsa w ogl w drzewie
        if (stmt.elseBranch == null) {
            return
        }

        // jesli jest else branch to on automatycznie sie wykona jesli kod tutaj dojdzie w ogl
        executeBlock(stmt.elseBranch)
    }

    private fun executeAssignment(stmt: Assignment) {
        if (!variables.containsKey(stmt.name)) {
            throw RuntimeException(
                "Błąd semantyczny, linia ${stmt.line}:\n" +
                        "Byt '${stmt.name}' nie został wcześniej powołany do życia."
            )
        }

        val value = evaluate(stmt.value)

        // samo stmt.value moze byc VariableExpr(name = "x", line = 3) lub NumberExpr(value = 10, line = 3) i td
        // wiec trzeba zrobic evaluate bo moze byc zmienna o wartosci np. 10 + 10

        variables[stmt.name] = value
    }

    private fun evaluate(expr: Expr): Any? {
        return when (expr) {
            is BinaryExpr -> {
                evaluateBinary(expr)
            }

            is NumberExpr -> {
                expr.value
            }

            is StringExpr -> {
                expr.value
            }

            is UnaryExpr -> {
                evaluateUnary(expr)
            }

            is VariableExpr -> {
                evaluateVariable(expr)
            }
        }
    }

    // odczytanie wartosc zmiennej z pamieci interpretera
    private fun evaluateVariable(expr: VariableExpr): Any? {
        if (!variables.containsKey(expr.name)) {
            throw RuntimeException(
                "Błąd semantyczny, linia ${expr.line}:\n" +
                        "Byt '${expr.name}' został użyty, choć nigdy nie został powołany do życia."
            )
        }

        if (variables[expr.name] == null) {
            throw RuntimeException(
                "Błąd semantyczny, linia ${expr.line}:\n" +
                        "Byt '${expr.name}' istnieje, ale nie otrzymał jeszcze wartości."
            )
        }
        return variables[expr.name]
    }

    // unary dziala tylko dla NIE, czyli zwraca bedzie zawsze tylko boolean
    private fun evaluateUnary(expr: UnaryExpr): Boolean {
        val value = evaluate(expr.right)
        if (value is Boolean) {
            return !value
        }
        throw RuntimeException(
            "Błąd typu, linia ${expr.line}:\n" +
                    "Operator 'nie' może być użyty tylko dla wartości logicznej."
        )
    }

    // x jest 10 - zwraca bool
    // 10 + 20   - zwraca int
    private fun evaluateBinary(expr: BinaryExpr): Any {
        val leftValue = evaluate(expr.left)
        val rightValue = evaluate(expr.right)

        val operator = expr.operator

        //JEST           -> left == right, zwracasz Boolean
        //NIE_JEST       -> left != right, zwracasz Boolean
        //PRZEWYŻSZA     -> left > right, zwracasz Boolean
        //USTĘPUJE       -> left < right, zwracasz Boolean
        //ORAZ           -> left && right, zwracasz Boolean
        //ALBO           -> left || right, zwracasz Boolean

        //PLUS           -> left + right, zwracasz Int
        //MINUS          -> left - right, zwracasz Int
        //STAR / RAZY    -> left * right, zwracasz Int
        //SLASH / DZIEL  -> left / right, zwracasz Int

        when (operator.type) {

            TokenType.JEST -> {
                return leftValue == rightValue
            }

            TokenType.NIE_JEST -> {
                return leftValue != rightValue
            }

            TokenType.PRZEWYŻSZA -> {
                if (leftValue is Int && rightValue is Int) {
                    return leftValue > rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator 'przewyższa' wymaga dwóch liczb całkowitych."
                )
            }

            TokenType.USTĘPUJE -> {
                if (leftValue is Int && rightValue is Int) {
                    return leftValue < rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator 'ustępuje' wymaga dwóch liczb całkowitych."
                )
            }

            TokenType.NIE_USTĘPUJE -> {
                if (leftValue is Int && rightValue is Int) {
                    return leftValue >= rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator 'nie_ustępuje' wymaga dwóch liczb całkowitych."
                )
            }

            TokenType.NIE_PRZEWYŻSZA -> {
                if (leftValue is Int && rightValue is Int) {
                    return leftValue <= rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator 'nie_przewyższa' wymaga dwóch liczb całkowitych."
                )
            }

            TokenType.ORAZ -> {
                if (leftValue is Boolean && rightValue is Boolean) {
                    return leftValue && rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator 'oraz' wymaga dwóch wartości logicznych."
                )
            }

            TokenType.ALBO -> {
                if (leftValue is Boolean && rightValue is Boolean) {
                    return leftValue || rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator 'albo' wymaga dwóch wartości logicznych."
                )
            }

            TokenType.PLUS -> {
                if (leftValue is Int && rightValue is Int) {
                    return leftValue + rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator '+' wymaga dwóch liczb całkowitych."
                )
            }

            TokenType.MINUS -> {
                if (leftValue is Int && rightValue is Int) {
                    return leftValue - rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator '-' wymaga dwóch liczb całkowitych."
                )
            }

            TokenType.STAR -> {
                if (leftValue is Int && rightValue is Int) {
                    return leftValue * rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator '*' wymaga dwóch liczb całkowitych."
                )
            }

            TokenType.SLASH -> {
                if (leftValue is Int && rightValue is Int) {
                    if (rightValue == 0) {
                        throw RuntimeException(
                            "Błąd wykonania, linia ${expr.line}:\n" +
                                    "Nie można dzielić przez zero."
                        )
                    }

                    return leftValue / rightValue
                }

                throw RuntimeException(
                    "Błąd typu, linia ${expr.line}:\n" +
                            "Operator '/' wymaga dwóch liczb całkowitych."
                )
            }

            else -> {
                throw RuntimeException(
                    "Błąd wykonania, linia ${expr.line}:\n" +
                            "Nieznany operator binarny '${operator.value}'."
                )
            }
        }

    }

    fun executeBlock(statements: List<Stmt>) {
        for (statement in statements) {
            execute(statement)
        }
    }
}