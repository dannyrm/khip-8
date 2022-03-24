package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.lineSeparator
import com.github.dannyrm.khip8.test.utils.BaseTest
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.expect

class DisplayMemoryUnitTest: BaseTest() {
    @MockK
    private lateinit var display: Display
    @InjectMockKs
    private lateinit var displayMemory: DisplayMemory

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
                    expect(true) { displayMemory[i, j] }
                } else {
                    expect(false) { displayMemory[i, j] }
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
                expect(false) { displayMemory[i, j] }
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
                expect(true) { displayMemory[i, j] }
            }
        }
    }

    @Test
    fun `Check setting works correctly for non clipping value`() {
        displayMemory[5,5] = 0xFFu

        for (i in 0..4) {
            expect(ZERO) { displayMemory.getRow(i) }
        }

        expect(0x7F8000000000000u) { displayMemory.getRow(5) }

        for (i in 6 until 32) {
            expect(ZERO) { displayMemory.getRow(i) }
        }
    }

    @Test
    fun `Check xoring works correctly in simple case`() {
        displayMemory[5,5] = 0xFFu

        for (i in 0 until 5) {
            expect(ZERO) { displayMemory.getRow(i) }
        }

        expect(0x07F8000000000000u) { displayMemory.getRow(5) }

        for (i in 6 until 32) {
            expect(ZERO) { displayMemory.getRow(i) }
        }

        displayMemory[5,5] = 0xFFu

        for (i in 0 until 32) {
            expect(ZERO) { displayMemory.getRow(i) }
        }
    }

    @Test
    fun `Check Set indicates a collision`() {
        // Initially no collision
        assertFalse { displayMemory.collision }

        displayMemory[5,5] = 0xFFu // 1111 1111
        assertFalse { displayMemory.collision }

        displayMemory[5,5] = 0xFFu // 1111 1111
        assertTrue { displayMemory.collision }

        displayMemory[5,5] = 0x01u // 0000 0001
        assertFalse { displayMemory.collision }

        displayMemory[5,5] = 0x01u // 0000 0001
        assertTrue { displayMemory.collision }

        displayMemory[5,5] = 0x0Au // 0000 1010
        assertFalse { displayMemory.collision }

        displayMemory[5,5] = 0x05u // 0000 0101
        assertFalse { displayMemory.collision }

        displayMemory[5,5] = 0x40u // 0100 0000
        assertFalse { displayMemory.collision }

        displayMemory[5,5] = 0x04u // 0000 0100
        assertTrue { displayMemory.collision }
    }

    @Test
    fun `Check setting works multiple values works correctly for non clipping value`() {
        displayMemory[5,5] = 0xFFu
        displayMemory[25,31] = 0xFFu

        for (i in 0..4) {
            expect(ZERO) { displayMemory.getRow(i) }
        }

        expect(0x7F8000000000000u) { displayMemory.getRow(5) }

        for (i in 6 until 31) {
            expect(ZERO) { displayMemory.getRow(i) }
        }

        expect(0x7f80000000u) { displayMemory.getRow(31) }
    }

    @Test
    fun `Check get pixel state works`() {
        displayMemory[5,5] = 0xFFu

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                if (x in 5..12 && y == 5) {
                    assertTrue { displayMemory[x, y] }
                } else {
                    assertFalse { displayMemory[x, y] }
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
                    assertTrue { displayMemory[x, y] }
                } else {
                    assertFalse { displayMemory[x, y] }
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
            expect(ZERO) { displayMemory.getRow(i) }
        }

        expect(0x1FEu) { displayMemory.getRow(5) }
        expect(0xFFu) { displayMemory.getRow(6) }
        expect(0x7Fu) { displayMemory.getRow(7) }
        expect(0x3Fu) { displayMemory.getRow(8) }
        expect(0x1Fu) { displayMemory.getRow(9) }
        expect(0xFu) { displayMemory.getRow(10) }
        expect(0x7u) { displayMemory.getRow(11) }
        expect(0x3u) { displayMemory.getRow(12) }
        expect(0x1u) { displayMemory.getRow(13) }

        for (i in 14 until 32) {
            expect(ZERO) { displayMemory.getRow(i) }
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
            expect(ZERO) { displayMemory.getRow(i) }
        }

        expect(0x1AEu) { displayMemory.getRow(5) }
        expect(0x101u) { displayMemory.getRow(6) }
        expect(0x3C0000000007Fu) { displayMemory.getRow(7) }

        for (i in 8 until 32) {
            expect(ZERO) { displayMemory.getRow(i) }
        }
    }

    @Test
    fun `Check get multiple pixel state values works correctly for clipped value`() {
        displayMemory[61,31] = 0xFFu

        for (y in 0 until MAX_HEIGHT_IN_BITS) {
            for (x in 0 until MAX_WIDTH_IN_BITS) {
                if ((x in 61..63 && y == 31)) {
                    assertTrue { displayMemory[x, y] }
                } else {
                    assertFalse { displayMemory[x, y] }
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

        expect(0x7F80000000000000u) { displayMemory.getRow(5) }
        expect(0x3FC0000000000000u) { displayMemory.getRow(10) }
    }

    @Test
    fun `Check dimensions`() {
        val (width, height) = displayMemory.dimensions()

        expect(64) { width }
        expect(32) { height }
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

        val nl = lineSeparator()

        expect(
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
                    "\t0000000000000000000000000111111110000000000000000000000000000000$nl"
        ) {
            displayMemory.toString()
        }
    }

    companion object {
        private val ZERO = 0.toULong()
        private const val MAX_WIDTH_IN_BITS = 64
        private const val MAX_HEIGHT_IN_BITS = 32
    }
}