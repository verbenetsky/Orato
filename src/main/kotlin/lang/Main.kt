package lang

import java.io.File

fun main() {
//    runFile("priorityInstruction.txt")

//    runFile("test1.txt")
//    runFile("test2.txt")
//    runFile("test3.txt")
    runFile("test4.txt")
//    runFile("test5.txt")
//    runFile("test6.txt")
}

fun runFile(fileName: String) {
    println("=== Uruchamiam plik: $fileName ===")

    val source = File(fileName).readText()

    val lexer = Lexer(source)
    val tokens = lexer.scanTokens()

//    println(tokens)

    val parser = Parser(tokens)
    val statements = parser.parse()

//    println(statements)

    val interpreter = Interpreter()
    interpreter.interpret(statements)

    println()
}