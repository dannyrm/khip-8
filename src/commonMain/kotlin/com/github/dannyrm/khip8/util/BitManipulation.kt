// Lowest 8 bits of the instruction (kk or byte)
fun rightByte(value: UInt): UByte = (value and 0xFFu).toUByte()

// Highest 4 bits of instruction (n or nibble)
fun leftNibble(value: UInt): UByte = (value and 0xF000u shr 12).toUByte()

// Lowest 4 bits of instruction (n or nibble)
fun rightNibble(value: UInt): UByte = (value and 0xFu).toUByte()

// Lower 4 bits of the high byte of instruction
fun x(value: UInt) : UByte = (value and 0xF00u shr 8).toUByte()

// Upper 4 bits of the low byte of instruction
fun y(value: UInt) : UByte = (value and 0xF0u shr 4).toUByte()

// Lowest 12 bits of instruction (nnn or addr)
fun rightNibbleByte(value: UInt): UInt = value and 0xFFFu

// Create a big endian 16 bit word from two 8 bit bytes
fun createBigEndianWordFromBytes(value1: UByte, value2: UByte): UInt = (value1.toUInt() shl 8) or value2.toUInt()

fun toHex(value: UInt, paddedLength: Int): String = "0x${convertBaseAndFormat(value, 16, paddedLength)}"
fun toBinary(value: UInt, paddedLength: Int): String = convertBaseAndFormat(value, 2, paddedLength)

fun toHex(value: UByte): String = toHex(value.toUInt(), 2)
fun wordHex(value: Int) = wordHex(value.toUInt())
fun wordHex(value: UInt) = toHex(value, 4)
fun nibbleByteHex(value: UInt) = toHex(value, 3)

fun toHexMinimal(value: UInt) = value.toString(16).uppercase()
fun toHexMinimal(value: UByte) = value.toString(16).uppercase()

// TODO: Test this.
private fun convertBaseAndFormat(value: UInt, base: Int, paddedLength: Int): String {
    var baseValue = value.toString(base).uppercase()

    for (i in baseValue.length until paddedLength) {
        baseValue = "0$baseValue"
    }

    return baseValue
}