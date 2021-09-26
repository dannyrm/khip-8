package uk.co.dmatthews.khip8.display

@OptIn(ExperimentalUnsignedTypes::class)
class Display(internal var buffer: Array<ULong> = Array(MAX_HEIGHT_IN_BITS) { 0.toULong() }) {

    fun clear() {
        for (i in 0 until MAX_HEIGHT_IN_BITS) {
            buffer[i] = 0.toULong()
        }
    }

    fun set(i: Int, j: Int, value: UByte) {
        // According to documentation:
        // If the program attempts to draw a sprite at an x coordinate greater than 0x3F, the x value will be reduced
        // modulo 64.
        // If the program attempts to draw at a y coordinate greater than 0x1F, the y value will be reduced modulo 32
        val adjustedI = i % MAX_WIDTH_IN_BITS
        val adjustedJ = j % MAX_HEIGHT_IN_BITS

        if (MAX_WIDTH_IN_BITS - adjustedI <= NUMBER_OF_BITS_IN_BYTE) {
            // Sprites that are drawn partially off-screen will be clipped.
            buffer[adjustedJ] xor (value.toULong() shr (MAX_WIDTH_IN_BITS - i))
        } else {
            // Shifts the byte to the correct position to perform the xor
            buffer[adjustedJ] xor (value.toULong() shl (MAX_WIDTH_IN_BITS - adjustedI - NUMBER_OF_BITS_IN_BYTE))
        }
    }

    fun getPixelState(i: Int, j: Int): Boolean {
        val mask = (0x1 shl MAX_WIDTH_IN_BITS - i - 1).toULong()

        val row = buffer[j]
        return (row and mask > 0.toULong())
    }

    companion object {
        private const val MAX_WIDTH_IN_BITS = 64
        private const val MAX_HEIGHT_IN_BITS = 32
        private const val NUMBER_OF_BITS_IN_BYTE = 8
    }
}