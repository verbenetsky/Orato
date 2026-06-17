# Orato

Orato is a simple interpreter written in Kotlin for a custom poetic programming language inspired by Polish literary syntax.

The language syntax was created using a literary style, so the commands resemble poetic sentences instead of typical programming keywords.

The interpreter reads source code, converts it into tokens using a lexer, builds an abstract syntax tree using a parser, and then executes the program.

## Documentation

Full project documentation is available in:

docs/documentation.pdf

The documentation is written in English.

## Features

* lexical analysis
* parsing into AST
* interpretation of source code
* variable declarations
* variable assignments
* integer and string values
* arithmetic expressions
* comparison operators
* logical operators
* if / else-if / else statements
* while loops
* input and output
* single-line and multi-line comments
* poetic and literary programming syntax

## Project structure

src/main/kotlin/

* Lexer.kt
* Parser.kt
* Ast.kt
* Interpreter.kt
* Token.kt
* TokenType.kt
* Main.kt

## How it works

The program is executed in the following stages:

source code
-> lexer
-> tokens
-> parser
-> AST
-> interpreter
-> program output

The lexer converts source code into tokens.

The parser checks the syntax and builds an abstract syntax tree.

The interpreter executes the AST and stores variables in memory.

## Example program

Niech kurtyna się podniesie

Oświadczam powołanie do życia bytu o imieniu x który od pierwszego tchnienia niesie wartość 10;

Oświadczam powołanie do życia bytu o imieniu tekst który od pierwszego tchnienia niesie wartość "Hello";

oznajmij(tekst);

oznajmij(x);

Wybaczcie śmiałość lecz rozważam czy przypadkiem (x jest 10) {
oznajmij("x is equal to 10");
}

Niech kurtyna opadnie

## Basic syntax

### Program start

Niech kurtyna się podniesie

### Program end

Niech kurtyna opadnie

### Variable declaration

Oświadczam powołanie do życia bytu o imieniu x;

### Variable declaration with value

Oświadczam powołanie do życia bytu o imieniu x który od pierwszego tchnienia niesie wartość 10;

### Assignment

Niech byt x zostanie obdarzony wartością 20;

### Output

oznajmij(x);

oznajmij("text");

### Input

Niech byt x przyjmie dar z ust użytkownika;

### If statement

Wybaczcie śmiałość lecz rozważam czy przypadkiem (x jest 10) {
oznajmij("true");
}

### Else-if

lecz jeśli los zechce (x jest 20) {
oznajmij("else-if");
}

### Else

Jeżeli nie to zaiste uczynię co innego {
oznajmij("else");
}

### While loop

Dopóki los pozwala (x ustępuje 5) {
oznajmij(x);
Niech byt x zostanie obdarzony wartością x + 1;
}

## Operators

### Arithmetic operators

* `+` addition
* `-` subtraction
* `*` multiplication
* `/` division

### Comparison operators

* `jest` means `==`
* `nie_jest` means `!=`
* `przewyższa` means `>`
* `ustępuje` means `<`
* `nie_ustępuje` means `>=`
* `nie_przewyższa` means `<=`

### Logical operators

* `oraz` means `&&`
* `albo` means `||`
* `nie` means `!`

## Comments

### Single-line comment

// comment

### Multi-line comment

/*
comment
*/

## Running the project

The easiest way to run the project is through IntelliJ IDEA.

To run the program:

1. Open the project in IntelliJ IDEA.
2. Go to the `Main.kt` file.
3. In `Main.kt`, uncomment the selected test file, for example `test1.txt`, `test2.txt`, etc.
4. You can also add your own `.txt` file with Orato source code.
5. Run the `main` function.

The selected file is passed through the lexer, parser and interpreter.

You can also run the project using Gradle:

./gradlew run

On Windows:

gradlew.bat run

## Running test files

There are six test files in the project:

* testy/test1.txt
* testy/test2.txt
* testy/test3.txt
* testy/test4.txt
* testy/test5.txt
* testy/test6.txt

These files contain examples of all possible language constructions available in Orato.

The test files include examples of variable declarations, assignments, arithmetic expressions, comparison operators, logical operators, conditional statements, loops, input, output and comments.

## Requirements

* Kotlin
* JDK 17 or newer
* Gradle

Full project documentation is available here:

[Open documentation](docs/documentation.pdf)
