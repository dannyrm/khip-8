package uk.co.dmatthews.khip8.memory

@OptIn(ExperimentalUnsignedTypes::class)
class DisplayMemory(internal var buffer: Array<ULong> = Array(MAX_HEIGHT_IN_BITS) { 0.toULong() }) {

    fun clear() {
        for (i in 0 until MAX_HEIGHT_IN_BITS) {
            buffer[i] = 0.toULong()
        }
    }

    operator fun set(x: Int, y: Int, value: UByte) {
        // According to documentation:
        // If the program attempts to draw a sprite at an x coordinate greater than 0x3F, the x value will be reduced
        // modulo 64.
        // If the program attempts to draw at a y coordinate greater than 0x1F, the y value will be reduced modulo 32
        val adjustedX = x % MAX_WIDTH_IN_BITS
        val adjustedY = y % MAX_HEIGHT_IN_BITS

        if (MAX_WIDTH_IN_BITS - adjustedX <= NUMBER_OF_BITS_IN_BYTE) {
            // Sprites that are drawn partially off-screen will be clipped.
            buffer[adjustedY] = buffer[adjustedY] xor (value.toULong() shr (MAX_WIDTH_IN_BITS - x))
        } else {
            // Shifts the byte to the correct position to perform the xor
            buffer[adjustedY] = buffer[adjustedY] xor (value.toULong() shl (MAX_WIDTH_IN_BITS - adjustedX - NUMBER_OF_BITS_IN_BYTE))
        }
    }

    fun getPixelState(x: Int, y: Int): Boolean {
        val mask = (0x1.toULong() shl (MAX_WIDTH_IN_BITS -1 - x)).toULong()

        val row = buffer[y]
        return ((row and mask) > 0.toULong())
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        for (y in buffer.indices) {
            var row = buffer[y].toString(2)

            for (i in MAX_WIDTH_IN_BITS downTo row.length+1) {
                row = "0$row"
            }

            stringBuilder.append("\t${row}${System.lineSeparator()}")
        }

        return stringBuilder.toString()
    }

    companion object {
        private const val MAX_WIDTH_IN_BITS = 64
        private const val MAX_HEIGHT_IN_BITS = 32
        private const val NUMBER_OF_BITS_IN_BYTE = 8
    }
}