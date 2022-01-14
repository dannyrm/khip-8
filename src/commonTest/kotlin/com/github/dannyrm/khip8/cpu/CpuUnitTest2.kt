package com.github.dannyrm.khip8.cpu

import com.github.dannyrm.khip8.config.MemoryConfig
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.executors.CpuInstructionExecutor
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.Stack
import com.github.dannyrm.khip8.memory.ValidatedMemory
import com.github.dannyrm.khip8.util.convertNumericParams
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.mockk.*
import kotlin.test.expect

@ExperimentalUnsignedTypes
class CpuUnitTest2: FunSpec({
    lateinit var chip8InputManager: Chip8InputManager
    lateinit var memoryManager: MemoryManager
    lateinit var instructionDecoder: InstructionDecoder
    lateinit var displayMemory: DisplayMemory
    lateinit var cpuInstructionExecutor: CpuInstructionExecutor
    val memoryConfig = MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)

    lateinit var cpu: Cpu

    val UNUSED_VALUE : UInt = 1u

    beforeTest {
        chip8InputManager = mockk(relaxed = true)
        memoryManager = mockk(relaxed = true)
        instructionDecoder = mockk(relaxed = true)
        displayMemory = mockk(relaxed = true)
        cpuInstructionExecutor = mockk(relaxed = true)

        cpu = Cpu(instructionDecoder, cpuInstructionExecutor, displayMemory, memoryManager, chip8InputManager, memoryConfig)
    }

    test("tick works correctly") {
        val nextInstruction: UInt = 0xE654u

        every { memoryManager.fetchNextInstruction() } returns nextInstruction

        cpu.tick()

        verify { chip8InputManager.lockInputs() }
        verify { memoryManager.fetchNextInstruction() }
        verify { instructionDecoder.decode(nextInstruction, listOf(cpuInstructionExecutor)) }
    }

    test("ret sets correct value to pc 00EE") {
        val stackValue = 42u

        val stack = mockk<Stack>()
        every { stack.pop() } returns stackValue
        every { memoryManager.stack } returns stack

        cpu.doReturn(UNUSED_VALUE)

        // Value popped from the stack set to the PC
        verify { stack.pop() }
        verify { memoryManager setProperty MemoryManager::pc.name value stackValue }
    }

    test("sys call does nothing 0nnn") {
        cpu.sysCall(UNUSED_VALUE)

        verify { listOf(chip8InputManager, memoryManager, instructionDecoder, displayMemory) wasNot Called }
        confirmVerified(chip8InputManager, memoryManager, instructionDecoder, displayMemory)
    }

    test("jmp sets correct value to pc 1NNN") {
        cpu.jump(0x1321u)

        verify { memoryManager setProperty MemoryManager::pc.name value 0x321u }
    }

    test("jmp sets correct value to pc if it overflows by 1 1NNN") {
        cpu.jump(0x10000u)

        verify { memoryManager setProperty MemoryManager::pc.name value 0u }
    }

    test("jmp sets correct value to pc if it overflows by 200 1NNN") {
        cpu.jump(0x10200u)

        verify { memoryManager setProperty MemoryManager::pc.name value 0x200u }
    }

    test("Clear screen calls the display to clear 00E0") {
        cpu.clearScreen(UNUSED_VALUE)

        verify { displayMemory.clear() }
    }

    context("Skip if register and byte are equal 3XNN") {
        withData("69AE,9,AE", "6F45,F,45", "60FF,0,FF", "6000,0,00") { input: String ->
            val (instruction: Int, registerLocation: Int, registerValue: Int) = convertNumericParams(input)

            val memory = mockk<ValidatedMemory>()

            every { memoryManager.registers } returns memory
            every { memory[registerLocation] } returns registerValue.toUByte()

            cpu.skipIfRegisterAndMemoryEqual(instruction.toUInt())

            verify { memoryManager.skipNextInstruction() }
        }
    }

    context("Do not Skip if register and byte are not equal 3XNN") {
        withData("6900,9,AE", "6F81,F,80", "6000,0,01") { input: String ->
            val (instruction: Int, registerLocation: Int, registerValue: Int) = convertNumericParams(input)

            val memory = mockk<ValidatedMemory>()
            every { memoryManager.registers } returns memory
            every { memory[registerLocation] } returns registerValue.toUByte()

            cpu.skipIfRegisterAndMemoryEqual(instruction.toUInt())

            verify(inverse = true) { memoryManager.skipNextInstruction() }
        }
    }

    context("Store BCD representation FX33") {
        withData("F533,5,AE,203,174", "FA33,A,0,205,0", "FA33,A,F,210,15", "FA33,A,3,210,3") { input: String ->
            val(instruction: Int, registerLocation: Int, registerValue: Int, iValue: Int, expectedResult: Int) = convertNumericParams(input, "hhhhi")

            val memory = mockk<ValidatedMemory>()
            every { memoryManager.registers } returns memory
            every { memory[registerLocation] } returns registerValue.toUByte()

            every { memoryManager.i } returns iValue.toUInt()

            val ram = ValidatedMemory(4096)
            every { memoryManager.ram } returns ram

            cpu.storeBCDRepresentation(instruction.toUInt())

            verify { memoryManager.i }

            val value = ram[iValue].toString() + ram[iValue+1].toString() + ram[iValue+2].toString()

            expect(expectedResult) { value.toInt() }
        }
    }

    test("Skip if register and byte are not equal 4XNN") {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xAu

        cpu.skipIfRegisterAndMemoryNotEqual(0x450Au)

        verify(exactly = 0) { memoryManager.skipNextInstruction() }
    }
})