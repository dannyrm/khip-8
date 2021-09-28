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
            displayMemory.buffer[i] = 0xFFu
        }

        displayMemory.clear()

        for (i in displayMemory.buffer.indices) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check setting works correctly for non clipping value`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFFu

        for (i in 0..4) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        expectThat(displayMemory.buffer[5]).isEqualTo(0x7F8000000000000u)

        for (i in 6 until 32) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check xoring works correctly in simple case`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFFu
        displayMemory[5,5] = 0xFFu

        for (i in 0 until 32) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check setting works multiple values works correctly for non clipping value`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFFu
        displayMemory[25,31] = 0xFFu

        for (i in 0..4) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        expectThat(displayMemory.buffer[5]).isEqualTo(0x7F8000000000000u)

        for (i in 6 until 31) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        expectThat(displayMemory.buffer[31]).isEqualTo(0x7f80000000u)
    }

    @Test
    fun `Check get pixel state works`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFFu

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
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
        displayMemory[5,5] = 0xFFu
        displayMemory[25,31] = 0xFFu

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                if ((x in 5..12 && y == 5) || (x in 25..32 && y == 31)) {
                    expectThat(displayMemory.getPixelState(x, y)).isTrue()
                } else {
                    expectThat(displayMemory.getPixelState(x, y)).isFalse()
                }
            }
        }
    }

    @Test
    fun `Check setting works correctly for clipped value`() {
        val displayMemory = DisplayMemory()
        displayMemory[55,5] = 0xFFu
        displayMemory[56,6] = 0xFFu
        displayMemory[57,7] = 0xFFu
        displayMemory[58,8] = 0xFFu
        displayMemory[59,9] = 0xFFu
        displayMemory[60,10] = 0xFFu
        displayMemory[61,11] = 0xFFu
        displayMemory[62,12] = 0xFFu
        displayMemory[63,13] = 0xFFu

        for (i in 0..4) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        expectThat(displayMemory.buffer[5]).isEqualTo(0x1FEu)
        expectThat(displayMemory.buffer[6]).isEqualTo(0xFFu)
        expectThat(displayMemory.buffer[7]).isEqualTo(0x7Fu)
        expectThat(displayMemory.buffer[8]).isEqualTo(0x3Fu)
        expectThat(displayMemory.buffer[9]).isEqualTo(0x1Fu)
        expectThat(displayMemory.buffer[10]).isEqualTo(0xFu)
        expectThat(displayMemory.buffer[11]).isEqualTo(0x7u)
        expectThat(displayMemory.buffer[12]).isEqualTo(0x3u)
        expectThat(displayMemory.buffer[13]).isEqualTo(0x1u)

        for (i in 14 until 32) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check complex xor`() {
        val displayMemory = DisplayMemory()
        displayMemory[55,5] = 0xFFu
        displayMemory[56,6] = 0xFFu
        displayMemory[57,7] = 0xFFu

        displayMemory[53,5] = 0x0Au
        displayMemory[55,6] = 0xFFu
        displayMemory[10,7] = 0xFu

        for (i in 0..4) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }

        expectThat(displayMemory.buffer[5]).isEqualTo(0x1AEu)
        expectThat(displayMemory.buffer[6]).isEqualTo(0x101u)
        expectThat(displayMemory.buffer[7]).isEqualTo(0x3C0000000007Fu)

        for (i in 8 until 32) {
            expectThat(displayMemory.buffer[i]).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check get multiple pixel state values works correctly for clipped value`() {
        val displayMemory = DisplayMemory()
        displayMemory[61,31] = 0xFFu

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                if ((x in 61..63 && y == 31)) {
                    expectThat(displayMemory.getPixelState(x, y)).isTrue()
                } else {
                    expectThat(displayMemory.getPixelState(x, y)).isFalse()
                }
            }
        }
    }

    @Test
    fun `Check address wrapping works correctly`() {
        val displayMemory = DisplayMemory()
        displayMemory[65,5] = 0xFFu // Two greater than the max width. Should wrap to position 1
        // Two greater than the max width. Should wrap to 2. 10 Greater than the max height - should wrap to 10
        displayMemory[66,42] = 0xFFu

        println(displayMemory)

        expectThat(displayMemory.buffer[5]).isEqualTo(0x7F80000000000000u)
        expectThat(displayMemory.buffer[10]).isEqualTo(0x3FC0000000000000u)
    }

    @Test
    fun `Check toString`() {
        val displayMemory = DisplayMemory()
        displayMemory[5,5] = 0xFFu
        displayMemory[25,31] = 0xFFu
        displayMemory[55,5] = 0xFFu
        displayMemory[56,6] = 0xFFu
        displayMemory[57,7] = 0xFFu
        displayMemory[58,8] = 0xFFu
        displayMemory[59,9] = 0xFFu
        displayMemory[60,10] = 0xFFu
        displayMemory[61,11] = 0xFFu
        displayMemory[62,12] = 0xFFu
        displayMemory[63,13] = 0xFFu

        println(displayMemory.toString())

        val nl = System.lineSeparator()

        expectThat(displayMemory.toString()).isEqualTo(
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000000$nl" +
                "\t0000011111111000000000000000000000000000000000000000000111111110$nl" +
                "\t0000000000000000000000000000000000000000000000000000000011111111$nl" +
                "\t0000000000000000000000000000000000000000000000000000000001111111$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000111111$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000011111$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000001111$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000111$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000011$nl" +
                "\t0000000000000000000000000000000000000000000000000000000000000001$nl" +
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