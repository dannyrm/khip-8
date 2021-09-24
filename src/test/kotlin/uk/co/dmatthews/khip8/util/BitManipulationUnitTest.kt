package uk.co.dmatthews.khip8.util

import createBigEndianWordFromBytes
import leftNibble
import org.junit.jupiter.api.Test
import rightByte
import rightNibble
import rightNibbleByte
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import x
import y

class BitManipulationUnitTest {
    @Test
    fun `Get Right most byte`() {
        expectThat(
            rightByte(0x4321.toUInt())
        ).isEqualTo(0x21.toUByte()
        )
    }

    @Test
    fun `Get left Nibble`() {
        expectThat(
            leftNibble(0x4321.toUInt())
        ).isEqualTo(0x4.toUByte()
        )
    }

    @Test
    fun `Get right Nibble`() {
        expectThat(
            rightNibble(0x4321.toUInt())
        ).isEqualTo(0x1.toUByte()
        )
    }

    @Test
    fun `Get x value`() {
        expectThat(
            x(0x4321.toUInt())
        ).isEqualTo(0x3.toUByte()
        )
    }

    @Test
    fun `Get y value`() {
        expectThat(
            y(0x4321.toUInt())
        ).isEqualTo(0x2.toUByte()
        )
    }

    @Test
    fun `Get right nibble bytes`() {
        expectThat(
            rightNibbleByte(0x4321.toUInt())
        ).isEqualTo(0x321.toUInt()
        )
    }

    @Test
    fun `Create big endian word`() {
        expectThat(
            createBigEndianWordFromBytes(0x43.toUByte(), 0x21.toUByte())
        ).isEqualTo(0x4321.toUInt()
        )
    }
}