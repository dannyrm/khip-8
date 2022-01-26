package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.util.lineSeparator
import kotlin.test.Test
import kotlin.test.assertFailsWith
import kotlin.test.expect

class ValidatedMemoryUnitTest {

    @Test
    fun `Store and retrieve values`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1u
        validatedMemory[2] = 2u

        expect(2u) { validatedMemory[2] }
        expect(1u) { validatedMemory[0] }
    }

    @Test
    fun `Store, replace and retrieve values`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1u
        validatedMemory[2] = 2u
        validatedMemory[0] = 3u
        validatedMemory[2] = 4u

        expect(4u) { validatedMemory[2] }
        expect(3u) { validatedMemory[0] }
    }

    @Test
    fun `Set value outside range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1u

        assertFailsWith<IllegalArgumentException> { 2.toUByte().also { validatedMemory[5] = it } }
        assertFailsWith<IllegalArgumentException> { 2.toUByte().also { validatedMemory[-1] = it } }
    }

    @Test
    fun `Get value outside range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1u

        assertFailsWith<IllegalArgumentException> { validatedMemory[5] }
        assertFailsWith<IllegalArgumentException> { validatedMemory[-1] }
    }

    @Test
    fun `Set and get values at extreme of range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 10u
        validatedMemory[3] = 5u

        expect(10u) { validatedMemory[0] }
        expect(5u) { validatedMemory[3] }
    }

    @Test
    fun `Clears memory`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 10u
        validatedMemory[1] = 15u
        validatedMemory[2] = 17u
        validatedMemory[3] = 5u

        validatedMemory.clear()

        expect(0u) { validatedMemory[0] }
        expect(0u) { validatedMemory[1] }
        expect(0u) { validatedMemory[2] }
        expect(0u) { validatedMemory[3] }
    }

    @Test
    fun `Check toString format with all empty memory`() {
        val validatedMemory = ValidatedMemory(45)

        expect(
            "\t0x0000 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00" + lineSeparator() +
                    "\t0x0014 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00" + lineSeparator() +
                    "\t0x0028 | 0x00 0x00 0x00 0x00 0x00" + lineSeparator()
        ) { validatedMemory.toString() }
    }

    @Test
    fun `Check toString format with partially completed memory`() {
        val validatedMemory = ValidatedMemory(45)
        validatedMemory[0] = 1u
        validatedMemory[42] = 5u

        expect(
            "\t0x0000 | 0x01 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00  *" + lineSeparator() +
                    "\t0x0014 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00" + lineSeparator() +
                    "\t0x0028 | 0x00 0x00 0x05 0x00 0x00                                                                             *" + lineSeparator()
        ) { validatedMemory.toString() }
    }
}