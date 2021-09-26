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
    fun `Check setting works correctly for clipping value`() {
        val displayMemory = DisplayMemory()
        displayMemory[55,5] = 0xFF.toUByte()
        displayMemory[56,6] = 0xFF.toUByte()
        displayMemory[57,7] = 0xFF.toUByte()
        displayMemory[58,8] = 0xFF.toUByte()
        displayMemory[59,9] = 0xFF.toUByte()
        displayMemory[60,10] = 0xFF.toUByte()
        displayMemory[61,11] = 0xFF.toUByte()
        displayMemory[62,12] = 0xFF.toUByte()
        displayMemory[63,13] = 0xFF.toUByte()

        for (i in 0..4) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        println(displayMemory)

        expectThat(displayMemory.buffer[5]).isEqualTo(0x1FE.toULong())
        expectThat(displayMemory.buffer[6]).isEqualTo(0xFF.toULong())
        expectThat(displayMemory.buffer[7]).isEqualTo(0x7F.toULong())
        expectThat(displayMemory.buffer[8]).isEqualTo(0x3F.toULong())
        expectThat(displayMemory.buffer[9]).isEqualTo(0x1F.toULong())
        expectThat(displayMemory.buffer[10]).isEqualTo(0xF.toULong())
        expectThat(displayMemory.buffer[11]).isEqualTo(0x7.toULong())
        expectThat(displayMemory.buffer[12]).isEqualTo(0x3.toULong())
        expectThat(displayMemory.buffer[13]).isEqualTo(0x1.toULong())

        for (i in 14 until 32) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
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