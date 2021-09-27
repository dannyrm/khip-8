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

    @ParameterizedTest
    @CsvSource(value = ["7510,5,30,40", "7F05,F,F4,F9", "7F05,F,FE,03", "7FFF,F,FF,FE"])
    fun `Add memory location to register 7XKK`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerLocation: Int,
                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerValueBefore: Int,
                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerValueAfter: Int) {
        every { memoryManager.registers[registerLocation] } returns registerValueBefore.toUByte()

        cpu.addValueToRegister(instruction.toUInt())

        verify { memoryManager.registers[registerLocation] = registerValueAfter.toUByte() }
    }

    @ParameterizedTest
    @CsvSource(value = ["8120,1,2,FE", "8F30,F,3,06"])
    fun `Load register y into register x 8XY0`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int) {
        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

        cpu.loadRegisterIntoRegister(instruction.toUInt())

        verify { memoryManager.registers[xRegisterLocation] = yRegisterValue.toUByte() }
    }

    @ParameterizedTest
    @CsvSource(value = ["8121,1,2,FE,45,FF","8FE1,F,E,FF,FF,FF","8FE1,F,E,01,08,09"])
    fun `or x and y then store in x 8XY1`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int) {
        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

        cpu.or(instruction.toUInt())

        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
    }

    @ParameterizedTest
    @CsvSource(value = ["8122,1,2,FE,45,44","8FE2,F,E,FF,FF,FF","8FE2,F,E,01,08,0", "8FE2,F,E,08,08,08"])
    fun `and x and y then store in x 8XY2`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int) {
        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

        cpu.and(instruction.toUInt())

        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
    }

    @ParameterizedTest
    @CsvSource(value = ["8123,1,2,FE,45,BB","8FE3,F,E,FF,FF,0","8FE3,F,E,01,08,09", "8FE3,F,E,08,08,0"])
    fun `xor x and y then store in x 8XY3`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int) {
        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

        cpu.xor(instruction.toUInt())

        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
    }

    @ParameterizedTest
    @CsvSource(value = ["8124,1,2,FE,45,43,01","8FE4,F,E,FF,FF,FE,01","8FE4,F,E,01,08,09,0", "8FE4,F,E,08,08,10,0",
                        "8FE4,F,E,F1,0F,0,1"])
    fun `add x and y then store in x 8XY4`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

        cpu.addRegisterAndRegister(instruction.toUInt())

        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
    }

    @ParameterizedTest
    @CsvSource(value = ["8125,1,2,FE,45,B9,1","8FE5,F,E,FF,FF,0,0","8FE5,F,E,09,08,01,1", "8FE5,F,E,08,08,0,0",
                        "8FE5,F,E,F1,0F,E2,1"])
    fun `subtract x and y then store in x 8XY5`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

        cpu.subtractYRegisterFromXRegister(instruction.toUInt())

        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
    }

    @ParameterizedTest
    @CsvSource(value = ["8126,1,2,45,22,1","8356,3,5,44,22,0","8356,3,5,FF,7F,1"])
    fun `right shift y and store in x 8XY6`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

        cpu.shiftRight(instruction.toUInt())

        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
    }

    @ParameterizedTest
    @CsvSource(value = ["8127,1,2,FE,45,47,0","8FE7,F,E,FF,FF,0,0","8FE7,F,E,09,08,FF,0", "8FE7,F,E,08,08,0,0",
                        "8FE7,F,E,F1,0F,1E,0","8FE7,F,E,0F,F1,E2,1","8127,1,2,45,FE,B9,1"])
    fun `subtract y and x then store in x 8XY7`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterResult: Int,
                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

        cpu.subtractXRegisterFromYRegister(instruction.toUInt())

        verify { memoryManager.registers[yRegisterLocation] = yRegisterResult.toUByte() }
        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
    }

    companion object {
        private val UNUSED_VALUE : UInt = 1.toUInt()
    }
}