package com.github.dannyrm.khip8.memory

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import java.lang.IllegalArgumentException

class ValidatedMemoryUnitTest {

    @Test
    fun `Store and retrieve values`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1u
        validatedMemory[2] = 2u

        expectThat(validatedMemory[2]).isEqualTo(2u)
        expectThat(validatedMemory[0]).isEqualTo(1u)
    }

    @Test
    fun `Store, replace and retrieve values`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1u
        validatedMemory[2] = 2u
        validatedMemory[0] = 3u
        validatedMemory[2] = 4u

        expectThat(validatedMemory[2]).isEqualTo(4u)
        expectThat(validatedMemory[0]).isEqualTo(3u)
    }

    @Test
    fun `Set value outside range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1u

        expectThrows<IllegalArgumentException> { 2.toUByte().also { validatedMemory[5] = it } }
        expectThrows<IllegalArgumentException> { 2.toUByte().also { validatedMemory[-1] = it } }
    }

    @Test
    fun `Get value outside range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1u

        expectThrows<IllegalArgumentException> { validatedMemory[5] }
        expectThrows<IllegalArgumentException> { validatedMemory[-1] }
    }

    @Test
    fun `Set and get values at extreme of range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 10u
        validatedMemory[3] = 5u

        expectThat(validatedMemory[0]).isEqualTo(10u)
        expectThat(validatedMemory[3]).isEqualTo(5u)
    }

    @Test
    fun `Clears memory`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 10u
        validatedMemory[1] = 15u
        validatedMemory[2] = 17u
        validatedMemory[3] = 5u

        validatedMemory.clear()

        expectThat(validatedMemory[0]).isEqualTo(0u)
        expectThat(validatedMemory[1]).isEqualTo(0u)
        expectThat(validatedMemory[2]).isEqualTo(0u)
        expectThat(validatedMemory[3]).isEqualTo(0u)
    }

    @Test
    fun `Check toString format with all empty memory`() {
        val validatedMemory = ValidatedMemory(45)

        expectThat(validatedMemory.toString()).isEqualTo("\t0x0000 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00" + System.lineSeparator() +
                "\t0x0014 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00" + System.lineSeparator() +
                "\t0x0028 | 0x00 0x00 0x00 0x00 0x00" + System.lineSeparator())
    }

    @Test
    fun `Check toString format with partially completed memory`() {
        val validatedMemory = ValidatedMemory(45)
        validatedMemory[0] = 1u
        validatedMemory[42] = 5u

        expectThat(validatedMemory.toString()).isEqualTo("\t0x0000 | 0x01 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00  *" + System.lineSeparator() +
                "\t0x0014 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00" + System.lineSeparator() +
                "\t0x0028 | 0x00 0x00 0x05 0x00 0x00                                                                             *" + System.lineSeparator())
    }
}