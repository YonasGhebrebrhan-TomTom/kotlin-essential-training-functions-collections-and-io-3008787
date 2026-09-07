// Numeric types in Kotlin: integer and floating-point types,
// signed vs unsigned values, and numeric type inference.


fun main() {
    // Numeric Types in Kotlin

    // 1. Integer Types
    // Kotlin provides several integer types, each with a specific size in bits:
    // - Byte: 8 bits, range from -128 to 127
    // - Short: 16 bits, range from -32,768 to 32,767
    // - Int: 32 bits, range from -2,147,483,648 to 2,147,483,647
    // - Long: 64 bits, range from -9,223,372,036,854,775,808 to 9,223,372,036,854,775,807

    val byteValue: Byte = 100
    val shortValue: Short = 10000
    val intValue: Int = 100000
    val longValue: Long = 10000000000L

    println("Byte Value: $byteValue")
    println("Short Value: $shortValue")
    println("Int Value: $intValue")
    println("Long Value: $longValue")

    // Unsigned Integer Types
    // Kotlin also provides unsigned integer types which can only represent non-negative values:
    // - UByte: 8 bits, range from 0 to 255
    // - UShort: 16 bits, range from 0 to 65,535
    // - UInt: 32 bits, range from 0 to 4,294,967,295
    // - ULong: 64 bits, range from 0 to 18,446,744,073,709,551,615

    val uByteValue: UByte = 200u
    val uShortValue: UShort = 60000u
    val uIntValue: UInt = 3000000000u
    val uLongValue: ULong = 10000000000000000000u

    println("UByte Value: $uByteValue")
    println("UShort Value: $uShortValue")
    println("UInt Value: $uIntValue")
    println("ULong Value: $uLongValue")

    // Difference between Signed and Unsigned Integers:
    // Signed integers can represent both negative and positive values,
    // while unsigned integers can only represent non-negative values.

    // 2. Floating-Point Types
    // - Float: 32 bits (single precision), about 6-7 decimal digits
    // - Double: 64 bits (double precision), about 15-16 decimal digits
    // Decimal literals are inferred as Double unless you add 'f'/'F'.

    val floatValue: Float = 3.1415927f
    val doubleValue: Double = 3.141592653589793

    println("Float Value: $floatValue")
    println("Double Value: $doubleValue")

    // Floating-point precision example (binary representation is approximate for many decimals)
    val floatCalc = 0.1f + 0.2f
    val doubleCalc = 0.1 + 0.2
    println("0.1f + 0.2f = $floatCalc")
    println("0.1 + 0.2 = $doubleCalc")

    // 3. Type Inference with Numeric Types
    // Kotlin infers types from literal form and range.
    val inferredInt = 42          // Int
    val inferredLong = 42L        // Long
    val inferredDouble = 2.5      // Double (default for decimal)
    val inferredFloat = 2.5f      // Float
    val inferredUInt = 42u        // UInt
    val inferredULong = 42uL      // ULong

    println("Inferred Int: $inferredInt")
    println("Inferred Long: $inferredLong")
    println("Inferred Double: $inferredDouble")
    println("Inferred Float: $inferredFloat")
    println("Inferred UInt: $inferredUInt")
    println("Inferred ULong: $inferredULong")
}