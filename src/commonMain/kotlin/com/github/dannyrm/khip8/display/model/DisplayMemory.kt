package com.github.dannyrm.khip8.display.model

import com.github.dannyrm.khip8.multiplatform.lineSeparator

class DisplayMemory(private val buffer: Array<ULong> = Array(MAX_HEIGHT_IN_BITS) { 0u },
                    var collision: Boolean = false) {

    fun clear() = buffer.fill(0u)
    fun dimensions() : IntArray = intArrayOf(MAX_WIDTH_IN_BITS, MAX_HEIGHT_IN_BITS)

    operator fun set(x: Int, y: Int, value: UByte) {
        // X or Y values outside the bounds of the display will wrap around to the other side.
        val adjustedX = x % MAX_WIDTH_IN_BITS
        val adjustedY = y % MAX_HEIGHT_IN_BITS

        val previousRowValues = buffer[adjustedY]
        val newRowValues = calculateNewRowValues(value.toULong(), adjustedX)

        buffer[adjustedY] = previousRowValues xor newRowValues

        // If any of the bits are the same in the previous and new row values then a collision has occurred.
        collision = (previousRowValues and newRowValues) > 0u
    }

    private fun calculateNewRowValues(value: ULong, x: Int): ULong {
        val numberOfBitsBeforeEndOfRow = MAX_WIDTH_IN_BITS - x

        return if (numberOfBitsBeforeEndOfRow <= NUMBER_OF_BITS_IN_BYTE) {
            // Sprites that are drawn partially off-screen will be clipped.
            (value shr (NUMBER_OF_BITS_IN_BYTE - numberOfBitsBeforeEndOfRow))
        } else {
            // Shifts the byte to the correct position to perform the xor
            (value shl (MAX_WIDTH_IN_BITS - x - NUMBER_OF_BITS_IN_BYTE))
        }
    }

    operator fun get(x: Int, y: Int): Boolean {
        val mask = (0x1.toULong() shl (MAX_WIDTH_IN_BITS -1 - x))

        val row = buffer[y]
        return ((row and mask) > 0u)
    }

    internal fun getRow(rowNum: Int) = buffer[rowNum]

    override fun toString(): String {
        val stringBuilder = StringBuilder()
        val paddingString = "0"

        buffer.forEach { row ->
            val binaryString = row.toString(2)
            stringBuilder.append(
                "\t${paddingString.repeat(MAX_WIDTH_IN_BITS - binaryString.length)}${binaryString}${lineSeparator()}"
            )
        }

        return stringBuilder.toString()
    }

    companion object {
        private const val MAX_WIDTH_IN_BITS = 64
        private const val MAX_HEIGHT_IN_BITS = 32
        private const val NUMBER_OF_BITS_IN_BYTE = 8
    }
}