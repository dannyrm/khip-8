package com.github.dannyrm.khip8.util

import createBigEndianWordFromBytes
import leftNibble
import nibbleByteHex
import rightByte
import rightNibble
import rightNibbleByte
import toHex
import toHexMinimal
import wordHex
import x
import y
import kotlin.test.Test
import kotlin.test.expect

class BitManipulationUnitTest {
    @Test
    fun `Get Right most byte`() {
        expect(0x21u) { rightByte(0x4321u) }
    }

    @Test
    fun `Get left Nibble`() {
        expect(0x4u) { leftNibble(0x4321u) }
    }

    @Test
    fun `Get right Nibble`() {
        expect(0x1u) { rightNibble(0x4321u) }
    }

    @Test
    fun `Get x value`() {
        expect(0x3u) { x(0x4321u) }
    }

    @Test
    fun `Get y value`() {
        expect(0x2u) { y(0x4321u) }
    }

    @Test
    fun `Get right nibble bytes`() {
        expect(0x321u) { rightNibbleByte(0x4321u) }
    }

    @Test
    fun `Create big endian word`() {
        expect(0x4321u) { createBigEndianWordFromBytes(0x43u, 0x21u) }
    }

    @Test
    fun `Int to hex converts Int to hex`() {
        expect("0x0F") { toHex(15u, 2) }
    }

    @Test
    fun `Int to hex with padding of 2`() {
        expect("0x01") { toHex(0x1u, 2) }
    }

    @Test
    fun `Int to hex with padding of 4`() {
        expect("0x0123") { toHex(0x123u, 4) }
    }

    @Test
    fun `Int to hex with padding of 4 and value matches limit`() {
        expect("0x1234") { toHex(0x1234u, 4) }
    }

    @Test
    fun `Int to hex with padding of 4 and value over limit`() {
        expect("0x12345") { toHex(0x12345u, 4) }
    }

    @Test
    fun `Byte to hex converts Byte to hex`() {
        expect("0x0F") { toHex(15u) }
    }

    @Test
    fun `wordHex converts with padding of 4`() {
        expect("0x0123") { wordHex(0x123u) }
    }

    @Test
    fun `wordHex does not cut off if value is larger`() {
        expect("0x12345") { wordHex(0x12345u) }
    }

    @Test
    fun `wordHex using Int converts with padding of 4`() {
        expect("0x0123") { wordHex(0x123) }
    }

    @Test
    fun `wordHex Using Int does not cut off if value is larger`() {
        expect("0x12345") { wordHex(0x12345) }
    }

    @Test
    fun `nibbleByteHex converts with padding of 3`() {
        expect("0x012") { nibbleByteHex(0x12u) }
    }

    @Test
    fun `nibbleByteHex does not cut off if value is larger`() {
        expect("0x12345") { nibbleByteHex(0x12345u) }
    }

    @Test
    fun `toHex minimal works correctly for two digit value=`() {
        expect("EF") { toHexMinimal(0xEFu) }
    }

    @Test
    fun `toHex minimal works correctly for five digit value=`() {
        expect("EF12A") { toHexMinimal(0xEF12Au) }
    }
}