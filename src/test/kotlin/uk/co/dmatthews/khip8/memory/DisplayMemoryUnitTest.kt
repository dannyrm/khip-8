package uk.co.dmatthews.khip8.memory

import uk.co.dmatthews.khip8.display.model.Display
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import uk.co.dmatthews.khip8.display.model.DisplayMemory

@ExtendWith(MockKExtension::class)
class DisplayMemoryUnitTest {
    @MockK private lateinit var display: Display
    @InjectMockKs private lateinit var displayMemory: DisplayMemory

    @Test
    fun `Check setting of the four corners of the display`() {
        displayMemory[0,0] = 128u
        displayMemory[63,0] = 128u
        displayMemory[0,31] = 128u
        displayMemory[63,31] = 128u

        val (width, height) = displayMemory.dimensions()

        for (i in 0 until width) {
            for (j in 0 until height) {
                if ((i==0 && j==0) || (i==63 && j==0) || (i==0 && j==31) || (i==63 && j==31)) {
                    expectThat(displayMemory[i, j]).isEqualTo(true)
                } else {
                    expectThat(displayMemory[i, j]).isEqualTo(false)
                }
            }
        }
    }

    @Test
    fun `check Display clears correctly`() {
        val (width, height) = displayMemory.dimensions()

        for (i in 0 until width) {
            for (j in 0 until height) {
                displayMemory[i, j] = 0x1u
            }
        }

        displayMemory.clear()

        for (i in 0 until width) {
            for (j in 0 until height) {
                expectThat(displayMemory[i, j]).isEqualTo(false)
            }
        }
    }

    @Test
    fun `Check memory is fully populated correctly`() {
        val (width, height) = displayMemory.dimensions()

        // Populate all but the last 8 pixels of each line
        for (i in 0 until width) {
            for (j in 0 until height) {
                displayMemory[i, j] = 0x1u
            }
        }

        for (i in 0 until height) {
            displayMemory[0, i] = 0xFEu
        }

        for (i in 0 until width) {
            for (j in 0 until height) {
                expectThat(displayMemory[i, j]).isEqualTo(true)
            }
        }
    }

    @Test
    fun `Check setting works correctly for non clipping value`() {
        displayMemory[5,5] = 0xFFu

        for (i in 0..4) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }

        expectThat(displayMemory.getRow(5)).isEqualTo(0x7F8000000000000u)

        for (i in 6 until 32) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check xoring works correctly in simple case`() {
        displayMemory[5,5] = 0xFFu

        for (i in 0 until 5) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }

        expectThat(displayMemory.getRow(5)).isEqualTo(0x07F8000000000000u)

        for (i in 6 until 32) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }

        displayMemory[5,5] = 0xFFu

        for (i in 0 until 32) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check Set indicates a collision`() {
        // Initially no collision
        expectThat(displayMemory.collision).isFalse()

        displayMemory[5,5] = 0xFFu // 1111 1111
        expectThat(displayMemory.collision).isFalse()

        displayMemory[5,5] = 0xFFu // 1111 1111
        expectThat(displayMemory.collision).isTrue()

        displayMemory[5,5] = 0x01u // 0000 0001
        expectThat(displayMemory.collision).isFalse()

        displayMemory[5,5] = 0x01u // 0000 0001
        expectThat(displayMemory.collision).isTrue()

        displayMemory[5,5] = 0x0Au // 0000 1010
        expectThat(displayMemory.collision).isFalse()

        displayMemory[5,5] = 0x05u // 0000 0101
        expectThat(displayMemory.collision).isFalse()

        displayMemory[5,5] = 0x40u // 0100 0000
        expectThat(displayMemory.collision).isFalse()

        displayMemory[5,5] = 0x04u // 0000 0100
        expectThat(displayMemory.collision).isTrue()
    }

    @Test
    fun `Check setting works multiple values works correctly for non clipping value`() {
        displayMemory[5,5] = 0xFFu
        displayMemory[25,31] = 0xFFu

        for (i in 0..4) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }

        expectThat(displayMemory.getRow(5)).isEqualTo(0x7F8000000000000u)

        for (i in 6 until 31) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }

        expectThat(displayMemory.getRow(31)).isEqualTo(0x7f80000000u)
    }

    @Test
    fun `Check get pixel state works`() {
        displayMemory[5,5] = 0xFFu

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                if (x in 5..12 && y == 5) {
                    expectThat(displayMemory[x, y]).isTrue()
                } else {
                    expectThat(displayMemory[x, y]).isFalse()
                }
            }
        }
    }

    @Test
    fun `Check get multiple pixel state values works correctly for non clipping value`() {
        displayMemory[5,5] = 0xFFu
        displayMemory[25,31] = 0xFFu

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                if ((x in 5..12 && y == 5) || (x in 25..32 && y == 31)) {
                    expectThat(displayMemory[x, y]).isTrue()
                } else {
                    expectThat(displayMemory[x, y]).isFalse()
                }
            }
        }
    }

    @Test
    fun `Check setting works correctly for clipped value`() {
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
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }

        expectThat(displayMemory.getRow(5)).isEqualTo(0x1FEu)
        expectThat(displayMemory.getRow(6)).isEqualTo(0xFFu)
        expectThat(displayMemory.getRow(7)).isEqualTo(0x7Fu)
        expectThat(displayMemory.getRow(8)).isEqualTo(0x3Fu)
        expectThat(displayMemory.getRow(9)).isEqualTo(0x1Fu)
        expectThat(displayMemory.getRow(10)).isEqualTo(0xFu)
        expectThat(displayMemory.getRow(11)).isEqualTo(0x7u)
        expectThat(displayMemory.getRow(12)).isEqualTo(0x3u)
        expectThat(displayMemory.getRow(13)).isEqualTo(0x1u)

        for (i in 14 until 32) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check complex xor`() {
        displayMemory[55,5] = 0xFFu
        displayMemory[56,6] = 0xFFu
        displayMemory[57,7] = 0xFFu

        displayMemory[53,5] = 0x0Au
        displayMemory[55,6] = 0xFFu
        displayMemory[10,7] = 0xFu

        for (i in 0..4) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }

        expectThat(displayMemory.getRow(5)).isEqualTo(0x1AEu)
        expectThat(displayMemory.getRow(6)).isEqualTo(0x101u)
        expectThat(displayMemory.getRow(7)).isEqualTo(0x3C0000000007Fu)

        for (i in 8 until 32) {
            expectThat(displayMemory.getRow(i)).isEqualTo(ZERO)
        }
    }

    @Test
    fun `Check get multiple pixel state values works correctly for clipped value`() {
        displayMemory[61,31] = 0xFFu

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                if ((x in 61..63 && y == 31)) {
                    expectThat(displayMemory[x, y]).isTrue()
                } else {
                    expectThat(displayMemory[x, y]).isFalse()
                }
            }
        }
    }

    @Test
    fun `Check address wrapping works correctly`() {
        displayMemory[65,5] = 0xFFu // Two greater than the max width. Should wrap to position 1
        // Two greater than the max width. Should wrap to 2. 10 Greater than the max height - should wrap to 10
        displayMemory[66,42] = 0xFFu

        println(displayMemory)

        expectThat(displayMemory.getRow(5)).isEqualTo(0x7F80000000000000u)
        expectThat(displayMemory.getRow(10)).isEqualTo(0x3FC0000000000000u)
    }

    @Test
    fun `Check dimensions`() {
        val (width, height) = displayMemory.dimensions()

        expectThat(width).isEqualTo(64)
        expectThat(height).isEqualTo(32)
    }

    @Test
    fun `Check toString`() {
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