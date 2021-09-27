package uk.co.dmatthews.khip8.cpu

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import uk.co.dmatthews.khip8.memory.MemoryManager
import uk.co.dmatthews.khip8.memory.Stack
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource
import uk.co.dmatthews.khip8.HexToIntegerCsvSourceArgumentConverter
import uk.co.dmatthews.khip8.memory.DisplayMemory
import uk.co.dmatthews.khip8.memory.ValidatedMemory

@ExtendWith(MockKExtension::class)
class CpuUnitTest {
    @InjectMockKs private lateinit var cpu: Cpu

    @MockK(relaxed = true) private lateinit var memoryManager: MemoryManager
    @MockK(relaxed = true) private lateinit var instructionDecoder: InstructionDecoder
    @MockK(relaxed = true) private lateinit var displayMemory: DisplayMemory

    @Test
    fun `ret sets correct value to pc 00EE`() {
        val stackValue = 42.toUInt()

        val stack = mockk<Stack>()
        every { stack.pop() } returns stackValue
        every { memoryManager.stack } returns stack

        cpu.doReturn(UNUSED_VALUE)

        // Value popped from the stack set to the PC
        verify { stack.pop() }
        verify { memoryManager setProperty MemoryManager::PC.name value stackValue }
    }

    @Test
    fun `jmp sets correct value to pc 1NNN`() {
        cpu.jump(0x1321.toUInt())

        verify { memoryManager setProperty MemoryManager::PC.name value 0x321.toUInt() }
    }

    @Test
    fun `Clear screen calls the display to clear 00E0`() {
        val displayMemory = mockk<DisplayMemory>(relaxed = true)
        every { memoryManager.displayMemory } returns displayMemory

        cpu.clearScreen(UNUSED_VALUE)

        verify { displayMemory.clear() }
    }

    @Test
    fun `Skip if register and byte are equal 3XNN`() {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xA.toUByte()

        cpu.skipIfRegisterAndMemoryEqual(0x350A.toUInt())

        verify { memoryManager.skipNextInstruction() }
    }

    @Test
    fun `Do not skip if register and byte are not equal 3XNN`() {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xB.toUByte()

        cpu.skipIfRegisterAndMemoryEqual(0x350A.toUInt())

        verify(exactly = 0) { memoryManager.skipNextInstruction() }
    }

    @Test
    fun `Skip if register and byte are not equal 4XNN`() {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xA.toUByte()

        cpu.skipIfRegisterAndMemoryNotEqual(0x450A.toUInt())

        verify(exactly = 0) { memoryManager.skipNextInstruction() }
    }

    @Test
    fun `Do not skip if register and byte are equal 4XNN`() {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xB.toUByte()

        cpu.skipIfRegisterAndMemoryNotEqual(0x450A.toUInt())

        verify { memoryManager.skipNextInstruction() }
    }

    @Test
    fun `Skip if register and register are equal 5XY0`() {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xA.toUByte()
        every { memory[8] } returns 0xA.toUByte()

        cpu.skipIfRegisterAndRegisterEqual(0x5580.toUInt())

        verify { memoryManager.skipNextInstruction() }
    }

    @Test
    fun `Do not skip if register and register are not equal 5XY0`() {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xA.toUByte()
        every { memory[8] } returns 0xB.toUByte()

        cpu.skipIfRegisterAndRegisterEqual(0x5580.toUInt())

        verify(exactly = 0) { memoryManager.skipNextInstruction() }
    }

    @Test
    fun `call puts pc on stack then sets pc to nnn 2NNN`() {
        val pcValue = 42.toUInt()

        val stack = mockk<Stack>()
        every { stack.push(pcValue) } just runs
        every { memoryManager.stack } returns stack
        every { memoryManager getProperty MemoryManager::PC.name } returns pcValue

        cpu.call(0x2321.toUInt())

        verify { stack.push(pcValue) }
        verify { memoryManager setProperty MemoryManager::PC.name value 0x321.toUInt() }
    }

    @ParameterizedTest
    @CsvSource(value = ["69AE,9,AE", "6F45,F,45"])
    fun `Load value into register 6XNN`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                        @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerLocation: Int,
                                        @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) value: Int) {
        cpu.loadMemoryIntoRegister(instruction.toUInt())

        verify { memoryManager.registers[registerLocation] = value.toUByte() }
    }

    companion object {
        private val UNUSED_VALUE : UInt = 1.toUInt()
    }
}