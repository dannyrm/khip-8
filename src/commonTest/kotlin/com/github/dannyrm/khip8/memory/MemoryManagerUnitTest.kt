package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.config.MemoryConfig
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.github.dannyrm.khip8.test.utils.BaseTest
import com.github.dannyrm.khip8.util.lineSeparator
import com.github.dannyrm.khip8.test.utils.TestFile
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.expect

@OptIn(ExperimentalUnsignedTypes::class)
class MemoryManagerUnitTest: BaseTest() {

    @Test
    fun `Load Program Into Memory`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = MemoryManager(mockk(), mockk(), memoryConfig)
        memoryManager.loadProgram(
            TestFile("inputs/15-puzzle.ch8", fromClasspath = true).getAbsolutePath()
        )

        val expectedOutput = hexToByteArray("inputs/15-puzzle.hex")

        // Program starts at 0x200 in memory.
        expectedOutput.forEachIndexed { index, byte ->
            expect(byte) { memoryManager.ram[0x200 + index] }
        }
    }

    @Test
    fun `Fetch next instruction`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = MemoryManager(mockk(), mockk(), memoryConfig)
        memoryManager.loadProgram(
            TestFile("inputs/15-puzzle.ch8", fromClasspath = true).getAbsolutePath()
        )

        expect(0x200u) { memoryManager.pc }

        var instruction = memoryManager.fetchNextInstruction()
        expect(0x00E0u) { instruction }
        expect(0x202u) { memoryManager.pc }

        instruction = memoryManager.fetchNextInstruction()
        expect(0x6C00u) { instruction }
        expect(0x204u) { memoryManager.pc }

        instruction = memoryManager.fetchNextInstruction()
        expect(0x4C00u) { instruction }
        expect(0x206u) { memoryManager.pc }
    }

    @Test
    fun `Check correct values after loading sprite data`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = MemoryManager(mockk(), mockk(), memoryConfig)
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
            expect(expectedValues[i]) { memoryManager.ram[i] }
        }
    }

    @Test
    fun `Check correct data for each sprite digit`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = MemoryManager(mockk(), mockk(), memoryConfig)
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

            expect(expectedValues[i][0]) { memoryManager.ram[digitStartLocation] }
            expect(expectedValues[i][1]) { memoryManager.ram[digitStartLocation + 1] }
            expect(expectedValues[i][2]) { memoryManager.ram[digitStartLocation + 2] }
            expect(expectedValues[i][3]) { memoryManager.ram[digitStartLocation + 3] }
            expect(expectedValues[i][4]) { memoryManager.ram[digitStartLocation + 4] }
        }
    }

    @Test
    fun `Check skip next instruction works as expected`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = MemoryManager(mockk(), mockk(), memoryConfig)
        expect(0x200u) { memoryManager.pc }
        memoryManager.skipNextInstruction()
        expect(0x202u) { memoryManager.pc }
    }

    @Test
    fun `Reset memory`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = MemoryManager(
            delayRegister = mockk(relaxed = true),
            soundRegister = mockk(relaxed = true),
            stack = mockk(relaxed = true),
            ram = mockk(relaxed = true),
            registers = mockk(relaxed = true),
            memoryConfig = memoryConfig
        )
        memoryManager.i = 0x50u
        memoryManager.pc = 0x800u

        memoryManager.resetMemory()

        expect(0u) { memoryManager.i }
        expect(0x200u) { memoryManager.pc }

        verify { memoryManager.delayRegister.clear() }
        verify { memoryManager.soundRegister.clear() }
        verify { memoryManager.stack.clear() }
        verify { memoryManager.ram.clear() }
        verify { memoryManager.registers.clear() }
    }

    @Test
    fun `check toString format`() {
        val soundTimerRegister = SoundTimerRegister(mockk(relaxed = true))
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = MemoryManager(
            ram = ValidatedMemory(42),
            soundRegister = soundTimerRegister,
            memoryConfig = memoryConfig,
            stack = Stack(16)
        )

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

        val newLine = lineSeparator()

        expect(
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
        ) {
            memoryManager.toString()
        }
    }

    /**
     * Reads in hex values, with each value separated by a space.
     */
    private fun hexToByteArray(fileName: String): UByteArray {
        val outputList: MutableList<String> = mutableListOf()

        TestFile(fileName, fromClasspath = true).asStringList().forEach {
            outputList.addAll(it.split(" "))
        }

        val outputByteArray = UByteArray(outputList.size)

        outputList.forEachIndexed { index, s ->
            outputByteArray[index] = s.toInt(16).toUByte()
        }

        return outputByteArray
    }
}