package memory

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import java.lang.IllegalArgumentException

class ValidatedMemoryUnitTest {

    @Test
    fun `Store and retrieve values`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1.toUByte()
        validatedMemory[2] = 2.toUByte()

        expectThat(validatedMemory[2]).isEqualTo(2.toUByte())
        expectThat(validatedMemory[0]).isEqualTo(1.toUByte())
    }

    @Test
    fun `Store, replace and retrieve values`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1.toUByte()
        validatedMemory[2] = 2.toUByte()
        validatedMemory[0] = 3.toUByte()
        validatedMemory[2] = 4.toUByte()

        expectThat(validatedMemory[2]).isEqualTo(4.toUByte())
        expectThat(validatedMemory[0]).isEqualTo(3.toUByte())
    }

    @Test
    fun `Set value outside range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1.toUByte()

        expectThrows<IllegalArgumentException> { 2.toUByte().also { validatedMemory[5] = it } }
        expectThrows<IllegalArgumentException> { 2.toUByte().also { validatedMemory[-1] = it } }
    }

    @Test
    fun `Get value outside range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 1.toUByte()

        expectThrows<IllegalArgumentException> { validatedMemory[5] }
        expectThrows<IllegalArgumentException> { validatedMemory[-1] }
    }

    @Test
    fun `Set and get values at extreme of range`() {
        val validatedMemory = ValidatedMemory(4)
        validatedMemory[0] = 10.toUByte()
        validatedMemory[3] = 5.toUByte()

        expectThat(validatedMemory[0]).isEqualTo(10.toUByte())
        expectThat(validatedMemory[3]).isEqualTo(5.toUByte())
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
        validatedMemory[0] = 1.toUByte()
        validatedMemory[42] = 5.toUByte()

        expectThat(validatedMemory.toString()).isEqualTo("\t0x0000 | 0x01 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00  *" + System.lineSeparator() +
                "\t0x0014 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00" + System.lineSeparator() +
                "\t0x0028 | 0x00 0x00 0x05 0x00 0x00                                                                             *" + System.lineSeparator())
    }
}