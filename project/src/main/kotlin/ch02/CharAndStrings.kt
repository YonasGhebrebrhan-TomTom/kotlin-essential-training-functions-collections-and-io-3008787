package ch02

fun main () {

    val aChar = 'a'
    val aNumberChar = '1'
    val newLineChar = '\n'
    println("Character: $aChar, Number Character: $aNumberChar, New Line Character: $newLineChar")

    val concatOption1   = "Hello, " + "World!"
    val concatOption2   = "Hello, ".plus("World!")
    val concatOption3   = "Hello, $concatOption1!"
    println("Concatenation: $concatOption1, $concatOption2, $concatOption3")

    val firstName = "John"
    val lastName = "Doe"
    val fullName = "$firstName $lastName"
    println("Full Name: $fullName")

    val age = 30
    println("Age: $age")

    val isAdult = age >= 18
    println("Is Adult: $isAdult")

    val raw = """
        This is a raw string.
            |It can span multiple lines.
        It preserves whitespace and formatting.
    """.trimMargin()
    println(raw)
}
