// Lowest 8 bits of the instruction (kk or byte)
fun rightByte(value: UInt): UByte = (value and 0xFF.toUInt()).toUByte()

// Highest 4 bits of instruction (n or nibble)
fun leftNibble(value: UInt): UByte = (value and 0xF000.toUInt() shr 12).toUByte()

// Lowest 4 bits of instruction (n or nibble)
fun rightNibble(value: UInt): UByte = (value and 0xF.toUInt()).toUByte()

// Lower 4 bits of the high byte of instruction
fun x(value: UInt) : UByte = (value and 0xF00.toUInt() shr 8).toUByte()

// Upper 4 bits of the low byte of instruction
fun y(value: UInt) : UByte = (value and 0xF0.toUInt() shr 4).toUByte()

// Lowest 12 bits of instruction (nnn or addr)
fun rightNibbleByte(value: UInt): UInt = value and 0xFFF.toUInt()

// Create a big endian 16 bit word from two 8 bit bytes
fun createBigEndianWordFromBytes(value1: UByte, value2: UByte): UInt {
    return (value1.toUInt() shl 8) or value2.toUInt()
}