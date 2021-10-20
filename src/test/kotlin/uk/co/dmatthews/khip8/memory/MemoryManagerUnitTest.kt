package uk.co.dmatthews.khip8.memory

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

@OptIn(ExperimentalUnsignedTypes::class)
class MemoryManagerUnitTest {

    @Test
    fun `Load Program Into Memory`() {
        val memoryManager = MemoryManager()
        memoryManager.loadProgram(
            loadFile("inputs/15-puzzle.ch8")
        )

        val expectedOutput = hexToByteArray("inputs/15-puzzle.hex")

        // Program starts at 0x200 in memory.
        expectedOutput.forEachIndexed { index, byte ->
            expectThat(memoryManager.ram[0x200+index]).isEqualTo(byte)
        }
    }

    @Test
    fun `Fetch next instruction`() {
        val memoryManager = MemoryManager()
        memoryManager.loadProgram(
            loadFile("inputs/15-puzzle.ch8")
        )

        expectThat(memoryManager.pc).isEqualTo(0x200u)

        var instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x00E0u)
        expectThat(memoryManager.pc).isEqualTo(0x202u)

        instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x6C00u)
        expectThat(memoryManager.pc).isEqualTo(0x204u)

        instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x4C00u)
        expectThat(memoryManager.pc).isEqualTo(0x206u)
    }

    @Test
    fun `Check correct values after loading sprite data`() {
        val memoryManager = MemoryManager()
        memoryManager.loadSpriteDigitsIntoMemory()

        val expectedValues = ubyteArrayOf(
            0xF0u, 0x90u, 0x90u, 0x90u, 0xF0u,
            0x20u, 0x60u, 0x20u, 0x20u, 0x70u,
            0xF0u, 0x10u, 0xF0u, 0x80u, 0xF0u,
            0xF0u, 0x10u, 0xF0u, 0x10u, 0xF0u,
            0x90u, 0x90u, 0xF0u, 0x10u, 0x10u,
            0xF0u, 0x80u, 0xF0u, 0x10u, 0xF0u,
            0xF0u, 0x80u, 0xF0u, 0x90u, 0xF0u,
            0xF0u, 0x10u, 0x20u, 0x40u, 0x40u,
            0xF0u, 0x90u, 0xF0u, 0x90u, 0xF0u,
            0xF0u, 0x90u, 0xF0u, 0x10u, 0xF0u,
            0xF0u, 0x90u, 0xF0u, 0x90u, 0x90u,
            0xE0u, 0x90u, 0xE0u, 0x90u, 0xE0u,
            0xF0u, 0x80u, 0x80u, 0x80u, 0xF0u,
            0xE0u, 0x90u, 0x90u, 0x90u, 0xE0u,
            0xF0u, 0x80u, 0xF0u, 0x80u, 0xF0u,
            0xF0u, 0x80u, 0xF0u, 0x80u, 0x80u,
        )

        for (i in 0 until 80) {
            expectThat(memoryManager.ram[i]).isEqualTo(expectedValues[i])
        }
    }

    @Test
    fun `Check correct data for each sprite digit`() {
        val memoryManager = MemoryManager()
        memoryManager.loadSpriteDigitsIntoMemory()

        val expectedValues = arrayOf(
            ubyteArrayOf(0xF0u, 0x90u, 0x90u, 0x90u, 0xF0u),
            ubyteArrayOf(0x20u, 0x60u, 0x20u, 0x20u, 0x70u),
            ubyteArrayOf(0xF0u, 0x10u, 0xF0u, 0x80u, 0xF0u),
            ubyteArrayOf(0xF0u, 0x10u, 0xF0u, 0x10u, 0xF0u),
            ubyteArrayOf(0x90u, 0x90u, 0xF0u, 0x10u, 0x10u),
            ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x10u, 0xF0u),
            ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x90u, 0xF0u),
            ubyteArrayOf(0xF0u, 0x10u, 0x20u, 0x40u, 0x40u),
            ubyteArrayOf(0xF0u, 0x90u, 0xF0u, 0x90u, 0xF0u),
            ubyteArrayOf(0xF0u, 0x90u, 0xF0u, 0x10u, 0xF0u),
            ubyteArrayOf(0xF0u, 0x90u, 0xF0u, 0x90u, 0x90u),
            ubyteArrayOf(0xE0u, 0x90u, 0xE0u, 0x90u, 0xE0u),
            ubyteArrayOf(0xF0u, 0x80u, 0x80u, 0x80u, 0xF0u),
            ubyteArrayOf(0xE0u, 0x90u, 0x90u, 0x90u, 0xE0u),
            ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x80u, 0xF0u),
            ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x80u, 0x80u)
        )

        for (i in 0 until 16) {
            val digitStartLocation = memoryManager.getLocationOfSpriteDigit(i.toUInt()).toInt()

            expectThat(memoryManager.ram[digitStartLocation]).isEqualTo(expectedValues[i][0])
            expectThat(memoryManager.ram[digitStartLocation+1]).isEqualTo(expectedValues[i][1])
            expectThat(memoryManager.ram[digitStartLocation+2]).isEqualTo(expectedValues[i][2])
            expectThat(memoryManager.ram[digitStartLocation+3]).isEqualTo(expectedValues[i][3])
            expectThat(memoryManager.ram[digitStartLocation+4]).isEqualTo(expectedValues[i][4])
        }
    }

    @Test
    fun `Check skip next instruction works as expected`() {
        val memoryManager = MemoryManager()
        expectThat(memoryManager.pc).isEqualTo(0x200u)
        memoryManager.skipNextInstruction()
        expectThat(memoryManager.pc).isEqualTo(0x202u)
    }

    @Test
    fun `Reset memory`() {
        val memoryManager = MemoryManager(delayRegister = mockk(relaxed = true), soundRegister = mockk(relaxed = true),
                                          stack = mockk(relaxed = true), ram = mockk(relaxed = true),
                                          registers = mockk(relaxed = true))

        memoryManager.i = 0x50u
        memoryManager.pc = 0x800u

        memoryManager.resetMemory()

        expectThat(memoryManager.i).isEqualTo(0u)
        expectThat(memoryManager.pc).isEqualTo(0x200u)

        verify { memoryManager.delayRegister.clear() }
        verify { memoryManager.soundRegister.clear() }
        verify { memoryManager.stack.clear() }
        verify { memoryManager.ram.clear() }
        verify { memoryManager.registers.clear() }
    }

    @Test
    fun `check toString format`() {
        val memoryManager = MemoryManager(ram = ValidatedMemory(42))

        memoryManager.ram[24] = 0x11u

        memoryManager.i = 0x55u
        memoryManager.delayRegister.value = 0x44u
        memoryManager.soundRegister.value = 0x33u

        memoryManager.registers[1] = 0x11u
        memoryManager.registers[3] = 0x22u
        memoryManager.registers[9] = 0x66u

        memoryManager.stack.push(0x77u)
        memoryManager.stack.push(0x88u)
        memoryManager.stack.push(0x99u)

        val newLine = System.lineSeparator()

        expectThat(memoryManager.toString()).isEqualTo(
            "Registers: {$newLine" +
                    "\tI = 0x0055, PC = 0x0200, DT = 0x44, ST = 0x33$newLine" +
                    "}$newLine" +
                    "General Registers: {$newLine" +
                    "\t0x0000 | 0x00 0x11 0x00 0x22 0x00 0x00 0x00 0x00 0x00 0x66 0x00 0x00 0x00 0x00 0x00 0x00                      *$newLine" +
                    "}$newLine" +
                    "Stack = {$newLine" +
                    "\tSize = 16, SP = 0x03$newLine" +
                    "\t----------$newLine" +
                    "\t| 0x0099 | $newLine" +
                    "\t| 0x0088 | $newLine" +
                    "\t| 0x0077 | $newLine" +
                    "\t----------$newLine" +
                    "}$newLine" +
                    "Ram = {$newLine" +
                    "\t0x0000 | 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00$newLine" +
                    "\t0x0014 | 0x00 0x00 0x00 0x00 0x11 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00 0x00  *$newLine" +
                    "\t0x0028 | 0x00 0x00$newLine" +
                    "}$newLine"
        )
    }

    private fun loadFile(fileName: String): File {
        return File(this.javaClass.classLoader.getResource(fileName).toURI())
    }

    /**
     * Reads in hex values, with each value separated by a space.
     */
    private fun hexToByteArray(fileName: String): UByteArray {
        val outputList: MutableList<String> = mutableListOf()

        val file = loadFile(fileName)
        file.forEachLine {
            outputList.addAll(it.split(" "))
        }

        val outputByteArray = UByteArray(outputList.size)

        outputList.forEachIndexed { index, s ->
            outputByteArray[index] = Integer.parseInt(s, 16) .toUByte()
        }

        return outputByteArray
    }
}