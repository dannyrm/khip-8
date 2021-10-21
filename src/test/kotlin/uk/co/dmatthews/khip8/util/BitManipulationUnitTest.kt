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
import toHexMinimal
import wordHex
import x
import y

class BitManipulationUnitTest {
    @Test
    fun `Get Right most byte`() {
        expectThat(
            rightByte(0x4321u)
        ).isEqualTo(0x21u)
    }

    @Test
    fun `Get left Nibble`() {
        expectThat(
            leftNibble(0x4321u)
        ).isEqualTo(0x4u
        )
    }

    @Test
    fun `Get right Nibble`() {
        expectThat(
            rightNibble(0x4321u)
        ).isEqualTo(0x1u
        )
    }

    @Test
    fun `Get x value`() {
        expectThat(
            x(0x4321u)
        ).isEqualTo(0x3u
        )
    }

    @Test
    fun `Get y value`() {
        expectThat(
            y(0x4321u)
        ).isEqualTo(0x2u
        )
    }

    @Test
    fun `Get right nibble bytes`() {
        expectThat(
            rightNibbleByte(0x4321u)
        ).isEqualTo(0x321u
        )
    }

    @Test
    fun `Create big endian word`() {
        expectThat(
            createBigEndianWordFromBytes(0x43u, 0x21u)
        ).isEqualTo(0x4321u
        )
    }

    @Test
    fun `Int to hex converts Int to hex`() {
        expectThat(toHex(15u, 2)).isEqualTo("0x0F")
    }

    @Test
    fun `Int to hex with padding of 2`() {
        expectThat(toHex(0x1u, 2)).isEqualTo("0x01")
    }

    @Test
    fun `Int to hex with padding of 4`() {
        expectThat(toHex(0x123u, 4)).isEqualTo("0x0123")
    }

    @Test
    fun `Int to hex with padding of 4 and value matches limit`() {
        expectThat(toHex(0x1234u, 4)).isEqualTo("0x1234")
    }

    @Test
    fun `Int to hex with padding of 4 and value over limit`() {
        expectThat(toHex(0x12345u, 4)).isEqualTo("0x12345")
    }

    @Test
    fun `Byte to hex converts Byte to hex`() {
        expectThat(toHex(15u)).isEqualTo("0x0F")
    }

    @Test
    fun `wordHex converts with padding of 4`() {
        expectThat(wordHex(0x123u)).isEqualTo("0x0123")
    }

    @Test
    fun `wordHex does not cut off if value is larger`() {
        expectThat(wordHex(0x12345u)).isEqualTo("0x12345")
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
        expectThat(nibbleByteHex(0x12u)).isEqualTo("0x012")
    }

    @Test
    fun `nibbleByteHex does not cut off if value is larger`() {
        expectThat(nibbleByteHex(0x12345u)).isEqualTo("0x12345")
    }

    @Test
    fun `toHex minimal works correctly for two digit value=`() {
        expectThat(toHexMinimal(0xEFu)).isEqualTo("EF")
    }

    @Test
    fun `toHex minimal works correctly for five digit value=`() {
        expectThat(toHexMinimal(0xEF12Au)).isEqualTo("EF12A")
    }
}