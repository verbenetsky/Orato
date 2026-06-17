# Orato

Orato is a simple interpreter written in Kotlin for a custom poetic programming language inspired by Polish literary syntax.

The language syntax was created using a literary style, so the commands resemble poetic sentences instead of typical programming keywords.

The interpreter reads source code, converts it into tokens using a lexer, builds an abstract syntax tree using a parser, and then executes the program.

## Documentation

Full project documentation is available here:

[Open documentation](docs/dokumentacja_jezyka_orato.pdf)

The documentation is written in English.

The documentation file should be placed in the `docs/` directory.

## Technologies used

The whole interpreter was written only in Kotlin.

No external lexer, parser or interpreter generator was used. The lexer, parser, AST and interpreter were implemented manually in Kotlin.

Gradle and JDK are used only to build and run the project.

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

```text
src/main/kotlin/
```

```text
Lexer.kt
Parser.kt
Ast.kt
Interpreter.kt
Token.kt
TokenType.kt
Main.kt
```

```text
docs/
```

```text
dokumentacja_jezyka_orato.pdf
```

```text
testy/
```

```text
test1.txt
test2.txt
test3.txt
test4.txt
test5.txt
test6.txt
```

## How it works

The program is executed in the following stages:

```text
source code
-> lexer
-> tokens
-> parser
-> AST
-> interpreter
-> program output
```

The lexer converts source code into tokens.

The parser checks the syntax and builds an abstract syntax tree.

The interpreter executes the AST and stores variables in memory.

## Example program

```orato
Niech kurtyna się podniesie

Oświadczam powołanie do życia bytu o imieniu x który od pierwszego tchnienia niesie wartość 10;

Oświadczam powołanie do życia bytu o imieniu tekst który od pierwszego tchnienia niesie wartość "Hello";

oznajmij(tekst);

oznajmij(x);

Wybaczcie śmiałość lecz rozważam czy przypadkiem (x jest 10) {
    oznajmij("x is equal to 10");
}

Niech kurtyna opadnie
```

## Basic syntax

### Program start

```orato
Niech kurtyna się podniesie
```

### Program end

```orato
Niech kurtyna opadnie
```

### Variable declaration

```orato
Oświadczam powołanie do życia bytu o imieniu x;
```

### Variable declaration with value

```orato
Oświadczam powołanie do życia bytu o imieniu x który od pierwszego tchnienia niesie wartość 10;
```

### Assignment

```orato
Niech byt x zostanie obdarzony wartością 20;
```

### Output

```orato
oznajmij(x);

oznajmij("text");
```

### Input

```orato
Niech byt x przyjmie dar z ust użytkownika;
```

### If statement

```orato
Wybaczcie śmiałość lecz rozważam czy przypadkiem (x jest 10) {
    oznajmij("true");
}
```

### Else-if statement

```orato
lecz jeśli los zechce (x jest 20) {
    oznajmij("else-if");
}
```

### Else statement

```orato
Jeżeli nie to zaiste uczynię co innego {
    oznajmij("else");
}
```

### While loop

```orato
Dopóki los pozwala (x ustępuje 5) {
    oznajmij(x);
    Niech byt x zostanie obdarzony wartością x + 1;
}
```

## Operators

### Arithmetic operators

* `+` addition
* `-` subtraction
* `*` multiplication
* `/` division

Example:

```orato
Niech byt x zostanie obdarzony wartością 10 + 5;
Niech byt y zostanie obdarzony wartością x * 2;
```

### Comparison operators

* `jest` means `==`
* `nie_jest` means `!=`
* `przewyższa` means `>`
* `ustępuje` means `<`
* `nie_ustępuje` means `>=`
* `nie_przewyższa` means `<=`

Example:

```orato
x jest 10
x nie_jest 20
x przewyższa 5
x ustępuje 100
x nie_ustępuje 10
x nie_przewyższa 50
```

### Logical operators

* `oraz` means `&&`
* `albo` means `||`
* `nie` means `!`

Example:

```orato
(x jest 10) oraz (y jest 20)
(x jest 10) albo (y jest 20)
nie (x jest 10)
```

## Comments

### Single-line comment

```orato
// comment
```

### Multi-line comment

```orato
/*
comment
*/
```

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

```bash
./gradlew run
```

On Windows:

```bash
gradlew.bat run
```

## Running test files

There are six test files in the project:

```text
testy/test1.txt
testy/test2.txt
testy/test3.txt
testy/test4.txt
testy/test5.txt
testy/test6.txt
```

These files contain examples of all possible language constructions available in Orato.

The test files include examples of variable declarations, assignments, arithmetic expressions, comparison operators, logical operators, conditional statements, loops, input, output and comments.

## Requirements

* Kotlin
* JDK 17 or newer
* Gradle
* IntelliJ IDEA
