package uk.co.dmatthews.khip8.memory

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

class MemoryManagerUnitTest {

    @Test
    fun `Load Program Into Memory`() {
        val memoryManager = MemoryManager()
        memoryManager.loadProgram(
            loadFile("inputs/15-puzzle.ch8")
        )

        val expectedOutput = hexToByteArray("outputs/15-puzzle.hex")

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

        expectThat(memoryManager.PC).isEqualTo(0x200.toUInt())

        var instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x00E0.toUInt())
        expectThat(memoryManager.PC).isEqualTo(0x202.toUInt())

        instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x6C00.toUInt())
        expectThat(memoryManager.PC).isEqualTo(0x204.toUInt())

        instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x4C00.toUInt())
        expectThat(memoryManager.PC).isEqualTo(0x206.toUInt())
    }

    @Test
    fun `Check correct values after loading sprite data`() {
        val memoryManager = MemoryManager()
        memoryManager.loadSpriteDigitsIntoMemory()

        println(memoryManager)

        val expectedValues = ubyteArrayOf(
            0xF0.toUByte(), 0x90.toUByte(), 0x90.toUByte(), 0x90.toUByte(), 0xF0.toUByte(),
            0x20.toUByte(), 0x60.toUByte(), 0x20.toUByte(), 0x20.toUByte(), 0x70.toUByte(),
            0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(),
            0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte(),
            0x90.toUByte(), 0x90.toUByte(), 0xF0.toUByte(), 0x10.toUByte(), 0x10.toUByte(),
            0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte(),
            0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte(),
            0xF0.toUByte(), 0x10.toUByte(), 0x20.toUByte(), 0x40.toUByte(), 0x40.toUByte(),
            0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte(),
            0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte(),
            0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte(), 0x90.toUByte(), 0x90.toUByte(),
            0xE0.toUByte(), 0x90.toUByte(), 0xE0.toUByte(), 0x90.toUByte(), 0xE0.toUByte(),
            0xF0.toUByte(), 0x80.toUByte(), 0x80.toUByte(), 0x80.toUByte(), 0xF0.toUByte(),
            0xE0.toUByte(), 0x90.toUByte(), 0x90.toUByte(), 0x90.toUByte(), 0xE0.toUByte(),
            0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(),
            0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(), 0x80.toUByte(), 0x80.toUByte(),
        )

        for (i in 0 until 80) {
            println(i)
            expectThat(memoryManager.ram[i]).isEqualTo(expectedValues[i])
        }
    }

    @Test
    fun `check toString format`() {
        val memoryManager = MemoryManager(ram = ValidatedMemory(42))

        memoryManager.ram[24] = 0x11.toUByte()

        memoryManager.I = 0x55.toUInt()
        memoryManager.delayRegister = 0x44.toUByte()
        memoryManager.soundRegister = 0x33.toUByte()

        memoryManager.registers[1] = 0x11.toUByte()
        memoryManager.registers[3] = 0x22.toUByte()
        memoryManager.registers[9] = 0x66.toUByte()

        memoryManager.stack.push(0x77.toUInt())
        memoryManager.stack.push(0x88.toUInt())
        memoryManager.stack.push(0x99.toUInt())

        println(memoryManager.toString())

        var newLine = System.lineSeparator()

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