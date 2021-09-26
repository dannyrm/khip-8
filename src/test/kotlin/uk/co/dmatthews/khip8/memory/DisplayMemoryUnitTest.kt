package uk.co.dmatthews.khip8.memory

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue

class DisplayMemoryUnitTest {

    @Test
    fun `check Display clears correctly`() {
        val displayMemory = DisplayMemory()

        for (i in displayMemory.buffer.indices) {
            displayMemory.buffer[i] = 0xFF.toULong()
        }

        displayMemory.clear()

        for (i in displayMemory.buffer.indices) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check setting works correctly for non clipping value`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFF.toUByte()

        for (i in 0..4) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        expectThat(displayMemory.buffer[5]).isEqualTo(0x7F8000000000000.toULong())

        for (i in 6 until 32) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check setting works multiple values works correctly for non clipping value`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFF.toUByte()
        displayMemory[25,31] = 0xFF.toUByte()

        for (i in 0..4) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        expectThat(displayMemory.buffer[5]).isEqualTo(0x7F8000000000000.toULong())

        for (i in 6 until 31) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        expectThat(displayMemory.buffer[31]).isEqualTo(0x7f80000000.toULong())
    }

    @Test
    fun `Check get pixel state works`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFF.toUByte()

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                println("$x, $y")
                if (x in 5..12 && y == 5) {
                    expectThat(displayMemory.getPixelState(x, y)).isTrue()
                } else {
                    expectThat(displayMemory.getPixelState(x, y)).isFalse()
                }
            }
        }
    }

    @Test
    fun `Check get multiple pixel state values works correctly for non clipping value`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFF.toUByte()
        displayMemory[25,31] = 0xFF.toUByte()

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                println("$x, $y")
                if ((x in 5..12 && y == 5) || (x in 25..32 && y == 31)) {
                    expectThat(displayMemory.getPixelState(x, y)).isTrue()
                } else {
                    expectThat(displayMemory.getPixelState(x, y)).isFalse()
                }
            }
        }
    }

    @Test
    fun `Check toString`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFF.toUByte()
        displayMemory[25,31] = 0xFF.toUByte()

        val nl = System.lineSeparator()

        expectThat(displayMemory.toString()).isEqualTo(
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000011111111000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000111111110000000000000000000000000000000$nl")
    }

    companion object {
        private val ZERO = 0.toULong()
        private const val MAX_WIDTH_IN_BITS = 64
        private const val MAX_HEIGHT_IN_BITS = 32
    }
}