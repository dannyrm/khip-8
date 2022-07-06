package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.config.MemoryConfig
import com.github.dannyrm.khip8.lineSeparator
import com.github.dannyrm.khip8.test.utils.BaseTest
import com.soywiz.korio.async.runBlockingNoSuspensions
import com.soywiz.korio.file.std.resourcesVfs
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue
import kotlin.test.expect

@OptIn(ExperimentalUnsignedTypes::class)
class MemoryManagerUnitTest: BaseTest() {

    @Test
    fun `Load Program Into Memory`() {
        val memoryConfig = memoryConfig()
        val memoryManager = MemoryManager(memoryConfig)

        val successfullyLoaded = runBlockingNoSuspensions {
            memoryManager.loadProgram(
                resourcesVfs["inputs/15-puzzle.ch8"].readAll()
            )
        }

        assertTrue { successfullyLoaded }

        val expectedOutput = hexToByteArray("inputs/15-puzzle.hex")

        // Program starts at 0x200 in memory.
        expectedOutput.forEachIndexed { index, byte ->
            expect(byte) { memoryManager.ram[0x200 + index] }
        }
    }

    @Test
    fun `loadProgram returns false if data is null`() {
        val memoryConfig = memoryConfig()
        val memoryManager = MemoryManager(memoryConfig)
        val successfullyLoaded = memoryManager.loadProgram(null)

        assertFalse { successfullyLoaded }
    }

    @Test
    fun `Fetch next instruction`() {
        val memoryConfig = memoryConfig()
        val memoryManager = MemoryManager(memoryConfig)

        runBlockingNoSuspensions {
            memoryManager.loadProgram(
                resourcesVfs["inputs/15-puzzle.ch8"].readAll()
            )
        }

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
        val memoryConfig = memoryConfig()
        val memoryManager = MemoryManager(memoryConfig)
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
        val memoryConfig = memoryConfig()
        val memoryManager = MemoryManager(memoryConfig)
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
        val memoryConfig = memoryConfig()
        val memoryManager = MemoryManager(memoryConfig)
        expect(0x200u) { memoryManager.pc }
        memoryManager.skipNextInstruction()
        expect(0x202u) { memoryManager.pc }
    }

    @Test
    fun `Reset memory`() {
        val memoryConfig = memoryConfig()
        val memoryManager = MemoryManager(
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

        verify { memoryManager.stack.clear() }
        verify { memoryManager.ram.clear() }
        verify { memoryManager.registers.clear() }

        // Reset also loads the sprite digits into memory
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
            verify { memoryManager.ram[i] = expectedValues[i] }
        }
    }

    @Test
    fun `check toString format`() {
        val memoryConfig = memoryConfig()
        val memoryManager = MemoryManager(
            ram = ValidatedMemory(42),
            memoryConfig = memoryConfig,
            stack = Stack(16)
        )

        memoryManager.ram[24] = 0x11u

        memoryManager.i = 0x55u

        memoryManager.registers[1] = 0x11u
        memoryManager.registers[3] = 0x22u
        memoryManager.registers[9] = 0x66u

        memoryManager.stack.push(0x77u)
        memoryManager.stack.push(0x88u)
        memoryManager.stack.push(0x99u)

        val newLine = lineSeparator()

        expect(
            "Registers: {$newLine" +
                    "\tI = 0x0055, PC = 0x0200$newLine" +
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

        runBlockingNoSuspensions {
            resourcesVfs[fileName].readLines().forEach {
                outputList.addAll(it.split(" "))
            }
        }

        outputList.removeIf {s -> s.trim().isEmpty() }

        val outputByteArray = UByteArray(outputList.size)

        outputList.forEachIndexed { index, s ->
            println(s)
            outputByteArray[index] = s.toInt(16).toUByte()
        }

        return outputByteArray
    }
}

private fun memoryConfig() = MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200, numberOfGeneralPurposeRegisters = 16)
