package com.github.dannyrm.khip8.cpu

import com.github.dannyrm.khip8.config.MemoryConfig
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.executors.CpuInstructionExecutor
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.Stack
import com.github.dannyrm.khip8.memory.ValidatedMemory
import com.github.dannyrm.khip8.test.utils.component6
import com.github.dannyrm.khip8.test.utils.component7
import com.github.dannyrm.khip8.test.utils.convertNumericParams
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.mockk.*
import kotlin.test.expect

@ExperimentalUnsignedTypes
class CpuUnitTest: FunSpec({
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

    test("Skip if register and register are equal 5XY0") {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xAu
        every { memory[8] } returns 0xAu

        cpu.skipIfRegisterAndRegisterEqual(0x5580u)

        verify { memoryManager.skipNextInstruction() }
    }

    test("Skip if register and register are not equal 9XY0") {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xAu
        every { memory[8] } returns 0xBu

        cpu.skipIfRegisterAndRegisterNotEqual(0x9580u)

        verify { memoryManager.skipNextInstruction() }
    }

    test("Do not skip if register and register are not equal 5XY0") {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xAu
        every { memory[8] } returns 0xBu

        cpu.skipIfRegisterAndRegisterEqual(0x5580u)

        verify(exactly = 0) { memoryManager.skipNextInstruction() }
    }

    test("Do not skip if register and register are equal 9XY0") {
        val memory = mockk<ValidatedMemory>()
        every { memoryManager.registers } returns memory
        every { memory[5] } returns 0xAu
        every { memory[8] } returns 0xAu

        cpu.skipIfRegisterAndRegisterNotEqual(0x9580u)

        verify(exactly = 0) { memoryManager.skipNextInstruction() }
    }

    test("call puts pc on stack then sets pc to nnn 2NNN") {
        val pcValue = 42u

        val stack = mockk<Stack>()
        every { stack.push(pcValue) } just runs
        every { memoryManager.stack } returns stack
        every { memoryManager getProperty MemoryManager::pc.name } returns pcValue

        cpu.call(0x2321u)

        verify { stack.push(pcValue) }
        verify { memoryManager setProperty MemoryManager::pc.name value 0x321u }
    }

    context("subtract y and x then store in x 8XY7") {
        withData("8127,1,2,FE,45,47,0","8FE7,F,E,FF,FF,0,0","8FE7,F,E,09,08,FF,0",
                 "8FE7,F,E,08,08,0,0", "8FE7,F,E,F1,0F,1E,0","8FE7,F,E,0F,F1,E2,1","8127,1,2,45,FE,B9,1","8127,1,2,F0,C3,D3,0") { input: String ->
            val(instruction: Int, xRegisterLocation: Int, yRegisterLocation: Int, xRegisterValue: Int, yRegisterValue: Int, xRegisterResult: Int, carryFlagResult: Int) = convertNumericParams(input)

            every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
            every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()

            cpu.subtractXRegisterFromYRegister(instruction.toUInt())

            verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
            verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
        }
    }

    context("Load memory into I register ANNN") {
        withData("A123,123","AFFF,FFF") { input: String ->
            val(instruction: Int, iRegisterValue: Int) = convertNumericParams(input)

            cpu.loadMemoryIntoIRegister(instruction.toUInt())

            verify { memoryManager.i = iRegisterValue.toUInt() }
        }
    }

    //
//    @ParameterizedTest
//    @CsvSource(value = ["69AE,9,AE", "6F45,F,45", "60FF,0,FF", "6000,0,00"])
//    fun `Load value into register 6XNN`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                        @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerLocation: Int,
//                                        @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) value: Int) {
//        cpu.loadMemoryIntoRegister(instruction.toUInt())
//
//        verify { memoryManager.registers[registerLocation] = value.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["7510,5,30,40", "7F05,F,F4,F9", "7F05,F,FE,03", "7FFF,F,FF,FE", "7E01,E,00,01", "7E03,E,FF,02"])
//    fun `Add value to register 7XKK`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                     @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerLocation: Int,
//                                     @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerValueBefore: Int,
//                                     @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerValueAfter: Int) {
//        every { memoryManager.registers[registerLocation] } returns registerValueBefore.toUByte()
//
//        cpu.addValueToRegister(instruction.toUInt())
//
//        verify { memoryManager.registers[registerLocation] = registerValueAfter.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["8120,1,2,FE", "8F30,F,3,06"])
//    fun `Load register y into register x 8XY0`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int) {
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        cpu.loadRegisterIntoRegister(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = yRegisterValue.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["8121,1,2,FE,45,FF","8FE1,F,E,FF,FF,FF","8FE1,F,E,01,08,09","8FE1,F,E,0F,F0,FF"])
//    fun `or x and y then store in x 8XY1`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
//                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
//                                          @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int) {
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        cpu.or(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["8122,1,2,FE,45,44","8FE2,F,E,FF,FF,FF","8FE2,F,E,01,08,0","8FE2,F,E,08,08,08","8452,4,5,0F,F1,01"])
//    fun `and x and y then store in x 8XY2`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int) {
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        cpu.and(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["8123,1,2,FE,45,BB","8FE3,F,E,FF,FF,0","8FE3,F,E,01,08,09","8763,7,6,0F,F1,FE"])
//    fun `xor x and y then store in x 8XY3`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int) {
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        cpu.xor(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["8124,1,2,FE,45,43,01","8FE4,F,E,FF,FF,FE,01","8FE4,F,E,01,08,09,0", "8FE4,F,E,08,08,10,0",
//                        "8FE4,F,E,F1,0F,0,1"])
//    fun `add x and y then store in x 8XY4`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        cpu.addRegisterAndRegister(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["8125,1,2,FE,45,B9,1","8FE5,F,E,FF,FF,0,0","8FE5,F,E,09,08,01,1","8FE5,F,E,08,08,0,0",
//                        "8FE5,F,E,F1,0F,E2,1","8855,8,5,FF,F1,0E,1"])
//    fun `subtract x and y then store in x 8XY5`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
//                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
//                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
//                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        cpu.subtractYRegisterFromXRegister(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["8126,1,2,45,22,1","8356,3,5,44,22,0","8356,3,5,FF,7F,1"])
//    fun `right shift y and store in x 8XY6`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
//                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
//                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        cpu.shiftRight(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["8126,1,45,22,1","8356,3,44,22,0","8356,3,FF,7F,1","8566,5,04,02,0","8566,5,05,02,1"])
//    fun `right shift x only variant and store in x 8XY6`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                                         @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                                         @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
//                                                         @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
//                                                         @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//
//        cpu.shiftRightXOnlyVariant(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["812E,1,2,45,8A,0","835E,3,5,F5,EA,1","835E,3,5,FF,FE,1"])
//    fun `left shift y and store in x 8XY6`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
//                                           @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        cpu.shiftLeft(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["812E,1,45,8A,0","835E,3,F5,EA,1","835E,3,FF,FE,1","835E,3,04,08,0","835E,3,84,08,1"])
//    fun `left shift x only variant and store in x 8XY6`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                                        @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                                        @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
//                                                        @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterResult: Int,
//                                                        @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) carryFlagResult: Int) {
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//
//        cpu.shiftLeftXOnlyVariant(instruction.toUInt())
//
//        verify { memoryManager.registers[xRegisterLocation] = xRegisterResult.toUByte() }
//        verify { memoryManager.registers[0xF] = carryFlagResult.toUByte() }
//    }
//
//
//
//    @ParameterizedTest
//    @CsvSource(value = ["B123,12,135","BFFF,FF,10FE","B001,01,02", "B2FC,04,300"])
//    fun `Jump with offset BNNN`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) v0RegisterValue: Int,
//                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) pcValue: Int) {
//        every { memoryManager.registers[0] } returns v0RegisterValue.toUByte()
//        cpu.jumpWithOffset(instruction.toUInt())
//
//        verify { memoryManager.pc = pcValue.toUInt() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["C100,1,0,0", "C500,5,0,0", "C501,5,0,1", "C503,5,0,3", "C50F,5,0,F"])
//    fun `Random with mask CXNN Ranges`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                       @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                       @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) resultRangeFrom: Int,
//                                       @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) resultRangeTo: Int) {
//        val memoryManager = MemoryManager(
//            soundRegister = mockk(),
//            memoryConfig = MemoryConfig(
//                memorySize = 4096,
//                stackSize = 16,
//                interpreterStartAddress = 0x0,
//                programStartAddress = 0x200
//            )
//        )
//        val cpu =
//            Cpu(instructionDecoder, cpuInstructionExecutor, displayMemory, memoryManager, chip8InputManager, mockk())
//
//        cpu.random(instruction.toUInt())
//
//        expectThat(memoryManager.registers[xRegisterLocation])
//            .isGreaterThanOrEqualTo(resultRangeFrom.toUByte())
//            .isLessThanOrEqualTo(resultRangeTo.toUByte())
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["C104,1,'0,4'", "C120,1,'0,20'", "C140,1,'0,40'", "CA15,A,'0,1,4,5,10,11,14,15'", "C380,3,'0,80'"])
//    fun `Random with mask CXNN Specific Values`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                                @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) vararg results: Int) {
//        val memoryConfig =
//            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
//        val memoryManager = MemoryManager(soundRegister = mockk(), memoryConfig = memoryConfig)
//        val cpu =
//            Cpu(instructionDecoder, cpuInstructionExecutor, displayMemory, memoryManager, chip8InputManager, mockk())
//
//        cpu.random(instruction.toUInt())
//
//        expectThat(memoryManager.registers[xRegisterLocation]).isContainedIn(results.map { it.toUByte() })
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["D135,1,3,5,F,FEEE"])
//    fun `Draw without collisions DXYN`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                       @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterLocation: Int,
//                                       @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterLocation: Int,
//                                       @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xRegisterValue: Int,
//                                       @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) yRegisterValue: Int,
//                                       @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) iRegisterValue: Int) {
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        every { memoryManager.i } returns iRegisterValue.toUInt()
//
//        val sprite = ubyteArrayOf(0x0u,  // 0000 0000
//                                  0x81u, // 1000 0001
//                                  0x81u, // 1000 0001
//                                  0x81u, // 1000 0001
//                                  0x0u)  // 0000 0000
//
//        every { memoryManager.ram[iRegisterValue] } returns sprite[0]
//        every { memoryManager.ram[iRegisterValue + 1] } returns sprite[1]
//        every { memoryManager.ram[iRegisterValue + 2] } returns sprite[2]
//        every { memoryManager.ram[iRegisterValue + 3] } returns sprite[3]
//        every { memoryManager.ram[iRegisterValue + 4] } returns sprite[4]
//
//        cpu.draw(instruction.toUInt())
//
//        verify { displayMemory[xRegisterValue, yRegisterValue] = sprite[0] }
//        verify { displayMemory[xRegisterValue, yRegisterValue + 1] = sprite[1] }
//        verify { displayMemory[xRegisterValue, yRegisterValue + 2] = sprite[2] }
//        verify { displayMemory[xRegisterValue, yRegisterValue + 3] = sprite[3] }
//        verify { displayMemory[xRegisterValue, yRegisterValue + 4] = sprite[4] }
//
//        verify { memoryManager.registers[0xF] = 0x0u }
//    }
//
//    @Test
//    fun `Draw with collisions DXYN`() {
//        val xRegisterLocation = 0x1
//        val yRegisterLocation = 0x3
//        val xRegisterValue = 0x5
//        val yRegisterValue = 0xF
//        val instruction = 0xD135
//        val iRegisterValue = 0xFEEE
//
//        every { displayMemory.collision } returns true
//
//        every { memoryManager.registers[xRegisterLocation] } returns xRegisterValue.toUByte()
//        every { memoryManager.registers[yRegisterLocation] } returns yRegisterValue.toUByte()
//
//        every { memoryManager.i } returns iRegisterValue.toUInt()
//
//        val sprite = ubyteArrayOf(0x0u,  // 0000 0000
//            0x81u, // 1000 0001
//            0x81u, // 1000 0001
//            0x81u, // 1000 0001
//            0x0u)  // 0000 0000
//
//        every { memoryManager.ram[iRegisterValue] } returns sprite[0]
//        every { memoryManager.ram[iRegisterValue + 1] } returns sprite[1]
//        every { memoryManager.ram[iRegisterValue + 2] } returns sprite[2]
//        every { memoryManager.ram[iRegisterValue + 3] } returns sprite[3]
//        every { memoryManager.ram[iRegisterValue + 4] } returns sprite[4]
//
//        cpu.draw(instruction.toUInt())
//
//        verify { displayMemory[xRegisterValue, yRegisterValue] = sprite[0] }
//        verify { displayMemory[xRegisterValue, yRegisterValue + 1] = sprite[1] }
//        verify { displayMemory[xRegisterValue, yRegisterValue + 2] = sprite[2] }
//        verify { displayMemory[xRegisterValue, yRegisterValue + 3] = sprite[3] }
//        verify { displayMemory[xRegisterValue, yRegisterValue + 4] = sprite[4] }
//
//        verify { memoryManager.registers[0xF] = 0x1u }
//    }
//
//    @Test
//    fun `Skip if key not pressed and key is not pressed EXA1`() {
//        every { memoryManager.registers[4] } returns 14u
//        every { chip8InputManager.isActive(14) } returns false
//
//        cpu.skipIfKeyNotPressed(0xE4A1u)
//
//        verify { memoryManager.skipNextInstruction() }
//    }
//
//    @Test
//    fun `Wait for key press FX0A`() {
//        every { memoryManager.registers[4] } returns 14u
//        every { chip8InputManager.isActive(14) } returns false
//
//        cpu.waitForKeyPress(0xF40Au)
//    }
//
//    @Test
//    fun `Skip if key not pressed and key is pressed EXA1`() {
//        every { memoryManager.registers[4] } returns 14u
//        every { chip8InputManager.isActive(14) } returns true
//
//        cpu.skipIfKeyNotPressed(0xE4A1u)
//
//        verify(inverse = true) { memoryManager.skipNextInstruction() }
//    }
//
//    @Test
//    fun `Skip if key pressed and key is not pressed EX9E`() {
//        every { memoryManager.registers[4] } returns 14u
//        every { chip8InputManager.isActive(14) } returns false
//
//        cpu.skipIfKeyPressed(0xE49Eu)
//
//        verify(inverse = true) { memoryManager.skipNextInstruction() }
//    }
//
//    @Test
//    fun `Skip if key pressed and key is pressed EX9E`() {
//        every { memoryManager.registers[4] } returns 14u
//        every { chip8InputManager.isActive(14) } returns true
//
//        cpu.skipIfKeyPressed(0xE49Eu)
//
//        verify { memoryManager.skipNextInstruction() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["FF07,F,33", "F207,2,43"])
//    fun `Set register to delay timer value FX07`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                                 @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xLocation: Int,
//                                                 @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) delayTimerValue: Int) {
//        every { memoryManager.delayRegister.value } returns delayTimerValue.toUByte()
//
//        cpu.setRegisterToDelayTimerValue(instruction.toUInt())
//
//        verify { memoryManager.registers[xLocation] = delayTimerValue.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["FF15,F,33", "F215,2,43", "FF15,F,FF"])
//    fun `Set delay timer value to register FX15`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                                 @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xLocation: Int,
//                                                 @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xValue: Int) {
//        every { memoryManager.registers[xLocation] } returns xValue.toUByte()
//
//        cpu.setDelayTimerRegisterToValueInGeneralRegister(instruction.toUInt())
//
//        verify { memoryManager.delayRegister.value = xValue.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["FF18,F,33", "F218,2,43"])
//    fun `Set sound timer value to register FX18`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                                 @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xLocation: Int,
//                                                 @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xValue: Int) {
//        every { memoryManager.registers[xLocation] } returns xValue.toUByte()
//
//        cpu.setSoundTimerRegisterToValueInGeneralRegister(instruction.toUInt())
//
//        verify { memoryManager.soundRegister.value = xValue.toUByte() }
//    }
//
//    @ParameterizedTest
//    @CsvSource(value = ["FF1E,F,33,FF,132", "F21E,2,43,1,44", "F01E,0,5,FFFF,5"])
//    fun `Add general register value to I FX1E`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
//                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xLocation: Int,
//                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) xValue: Int,
//                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) originalIValue: Int,
//                                               @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) resultIValue: Int) {
//        every { memoryManager.registers[xLocation] } returns xValue.toUByte()
//        every { memoryManager.i } returns originalIValue.toUInt()
//
//        cpu.addGeneralRegisterToIRegister(instruction.toUInt())
//
//        verify { memoryManager.i = resultIValue.toUInt() }
//    }
})