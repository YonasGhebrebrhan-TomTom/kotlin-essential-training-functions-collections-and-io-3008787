package ch02

fun main() {
    // Signed integer types
    val anInt: Int = 42
    val aLong: Long = 1234567890123456789L
    val aShort: Short = 32767
    val aByte: Byte = 127
    println("Signed integers: $anInt, $aLong, $aShort, $aByte")

    // Floating-point types
    val aDouble: Double = 3.14
    val aFloat: Float = 2.71828f
    println("Floating-point: $aDouble, $aFloat")

    // Unsigned types
    val unsignedInt: UInt = 10U
    val unsignedLong: ULong = 0UL
    println("Unsigned: $unsignedInt, $unsignedLong")

    // Boolean and comparisons
    println("Boolean: ${false}")
    println("Comparisons: ${10f > 1}, ${10.1 <= 10.2}")

    // Type conversions
    println("Conversions: ${10.5.toInt()}, ${10.9.toFloat()}, ${5.toULong()}, ${1_234_567_890.toByte()}")
}
