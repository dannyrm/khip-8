package uk.co.dmatthews.khip8.util

import createBigEndianWordFromBytes
import leftNibble
import nibbleByteHex
import org.junit.jupiter.api.Test
import rightByte
import rightNibble
import rightNibbleByte
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import toHex
import wordHex
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

    @Test
    fun `Int to hex converts Int to hex`() {
        expectThat(toHex(15.toUInt(), 2)).isEqualTo("0x0F")
    }

    @Test
    fun `Int to hex with padding of 2`() {
        expectThat(toHex(0x1.toUInt(), 2)).isEqualTo("0x01")
    }

    @Test
    fun `Int to hex with padding of 4`() {
        expectThat(toHex(0x123.toUInt(), 4)).isEqualTo("0x0123")
    }

    @Test
    fun `Int to hex with padding of 4 and value matches limit`() {
        expectThat(toHex(0x1234.toUInt(), 4)).isEqualTo("0x1234")
    }

    @Test
    fun `Int to hex with padding of 4 and value over limit`() {
        expectThat(toHex(0x12345.toUInt(), 4)).isEqualTo("0x12345")
    }

    @Test
    fun `Byte to hex converts Byte to hex`() {
        expectThat(toHex(15.toUByte())).isEqualTo("0x0F")
    }

    @Test
    fun `wordHex converts with padding of 4`() {
        expectThat(wordHex(0x123.toUInt())).isEqualTo("0x0123")
    }

    @Test
    fun `wordHex does not cut off if value is larger`() {
        expectThat(wordHex(0x12345.toUInt())).isEqualTo("0x12345")
    }

    @Test
    fun `wordHex using Int converts with padding of 4`() {
        expectThat(wordHex(0x123)).isEqualTo("0x0123")
    }

    @Test
    fun `wordHex Using Int does not cut off if value is larger`() {
        expectThat(wordHex(0x12345)).isEqualTo("0x12345")
    }

    @Test
    fun `nibbleByteHex converts with padding of 3`() {
        expectThat(nibbleByteHex(0x12.toUInt())).isEqualTo("0x012")
    }

    @Test
    fun `nibbleByteHex does not cut off if value is larger`() {
        expectThat(nibbleByteHex(0x12345.toUInt())).isEqualTo("0x12345")
    }
}