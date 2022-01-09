package com.github.dannyrm.khip8.cpu

import com.github.dannyrm.khip8.HexToIntegerCsvSourceArgumentConverter
import com.github.dannyrm.khip8.config.MemoryConfig
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.InstructionDecoder
import com.github.dannyrm.khip8.executors.CpuInstructionExecutor
import com.github.dannyrm.khip8.memory.MemoryManager
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.converter.ConvertWith
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import com.github.dannyrm.khip8.util.FeatureManager
import com.github.dannyrm.khip8.util.SystemMode

class CpuIntegrationTest {

    @Test
    fun `syscall works correctly`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        // Jp to memory instruction
        memoryManager.ram[0x200] = 0x05u
        memoryManager.ram[0x201] = 0x55u

        val instructionDecoder = InstructionDecoder()

        val cpu = Cpu(
            instructionDecoder,
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        cpu.tick()

        expectThat(memoryManager.pc).isEqualTo(0x202u)
    }

    @Test
    fun `jump to memory location works correctly`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        // Jp to memory instruction
        memoryManager.ram[0x200] = 0x15u
        memoryManager.ram[0x201] = 0x55u

        val instructionDecoder = InstructionDecoder()
        val cpuInstructionExecutor = CpuInstructionExecutor()

        val cpu = Cpu(
            instructionDecoder,
            cpuInstructionExecutor,
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )
        cpuInstructionExecutor.init(cpu)

        cpu.tick()

        expectThat(memoryManager.pc).isEqualTo(0x555u)
    }

    @Test
    fun `Add value to register sequence 7XKK`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        val instruction1 = 0x7510
        val instruction2 = 0x7F05
        val instruction3 = 0x7F05
        val instruction4 = 0x7FFF
        val instruction5 = 0x7E01
        val instruction6 = 0x7101
        val instruction7 = 0x7101
        val instruction8 = 0x7101

        cpu.addValueToRegister(instruction1.toUInt())
        cpu.addValueToRegister(instruction2.toUInt())
        cpu.addValueToRegister(instruction3.toUInt())
        cpu.addValueToRegister(instruction4.toUInt())
        cpu.addValueToRegister(instruction5.toUInt())
        cpu.addValueToRegister(instruction6.toUInt())
        cpu.addValueToRegister(instruction7.toUInt())
        cpu.addValueToRegister(instruction8.toUInt())

        expectThat(memoryManager.registers[0x1]).isEqualTo(0x03.toUByte()) // 01 + 01 + 01 = 03
        expectThat(memoryManager.registers[0x5]).isEqualTo(0x10.toUByte())
        expectThat(memoryManager.registers[0xF]).isEqualTo(0x09.toUByte()) // 05 + 05 + FF = 09
        expectThat(memoryManager.registers[0xE]).isEqualTo(0x01.toUByte())
    }

    @Test
    fun `Add value with wrapping 7XKK`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        val instruction1 = 0x7503
        val addFFInstruction = 0x75FF

        cpu.addValueToRegister(instruction1.toUInt())

        expectThat(memoryManager.registers[0x5]).isEqualTo(0x03.toUByte())

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expectThat(memoryManager.registers[0x5]).isEqualTo(0x02.toUByte())

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expectThat(memoryManager.registers[0x5]).isEqualTo(0x01.toUByte())

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expectThat(memoryManager.registers[0x5]).isEqualTo(0x00.toUByte())

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expectThat(memoryManager.registers[0x5]).isEqualTo(0xFF.toUByte())

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expectThat(memoryManager.registers[0x5]).isEqualTo(0xFE.toUByte())
    }

    @Test
    fun `Load register to register sequence 8XK0`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        val instruction0 = 0x7510u
        val instruction01 = 0x79FFu

        val instruction1 = 0x8650u
        val instruction2 = 0x8F00u
        val instruction3 = 0x8F60u
        val instruction4 = 0x8FF0u
        val instruction5 = 0x8E90u

        cpu.addValueToRegister(instruction0)

        expectThat(memoryManager.registers[0x5]).isEqualTo(0x10.toUByte())

        cpu.addValueToRegister(instruction01)

        expectThat(memoryManager.registers[0x9]).isEqualTo(0xFF.toUByte())

        cpu.loadRegisterIntoRegister(instruction1)

        expectThat(memoryManager.registers[0x6]).isEqualTo(0x10.toUByte())

        cpu.loadRegisterIntoRegister(instruction2)

        expectThat(memoryManager.registers[0xF]).isEqualTo(0x00.toUByte())

        cpu.loadRegisterIntoRegister(instruction3)

        expectThat(memoryManager.registers[0xF]).isEqualTo(0x10.toUByte())

        cpu.loadRegisterIntoRegister(instruction4)

        expectThat(memoryManager.registers[0xF]).isEqualTo(0x10.toUByte())

        cpu.loadRegisterIntoRegister(instruction5)

        expectThat(memoryManager.registers[0xE]).isEqualTo(0xFF.toUByte())
    }


    @ParameterizedTest
    @CsvSource(value = ["F029,0,0,0","F129,1,1,5","F229,2,2,A","F329,3,3,F","F429,4,4,14","F529,5,5,19","F629,6,6,1E",
                        "F729,7,7,23","F829,8,8,28","F929,9,9,2D","FA29,A,A,32","FB29,B,B,37","FC29,C,C,3C",
                        "FD29,D,D,41","FE29,E,E,46","FF29,F,F,4B","F529,5,0,0","F529,5,F,4B"])
    fun `Get location of sprite digit FX29`(@ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) instruction: Int,
                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerNumber: Int,
                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) registerValue: Int,
                                            @ConvertWith(HexToIntegerCsvSourceArgumentConverter::class) memoryLocation: Int) {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        memoryManager.registers[registerNumber] = registerValue.toUByte()

        cpu.loadIRegisterWithLocationOfSpriteForDigit(instruction.toUInt())

        expectThat(memoryManager.i).isEqualTo(memoryLocation.toUInt())
    }

    @Test
    fun `Load general registers into memory FX55`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        memoryManager.registers[0x0] = 0u
        memoryManager.registers[0x1] = 1u
        memoryManager.registers[0x2] = 2u
        memoryManager.registers[0x3] = 3u
        memoryManager.registers[0x4] = 4u

        memoryManager.i = 0x500u

        cpu.loadAllGeneralRegistersIntoMemory(0xF455u)

        expectThat(memoryManager.ram[0x500]).isEqualTo(0u)
        expectThat(memoryManager.ram[0x501]).isEqualTo(1u)
        expectThat(memoryManager.ram[0x502]).isEqualTo(2u)
        expectThat(memoryManager.ram[0x503]).isEqualTo(3u)
        expectThat(memoryManager.ram[0x504]).isEqualTo(4u)

        expectThat(memoryManager.i).isEqualTo(0x500u)
    }

    @ParameterizedTest
    @EnumSource(value = SystemMode::class, names = ["CHIP_8_MODE", "CHIP_48_MODE"])
    fun `Load general registers into memory in Chip8 or Chip 48 Mode FX55`(systemMode: SystemMode) {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        try {
            FeatureManager.systemMode = systemMode

            memoryManager.registers[0x0] = 0u
            memoryManager.registers[0x1] = 1u
            memoryManager.registers[0x2] = 2u
            memoryManager.registers[0x3] = 3u
            memoryManager.registers[0x4] = 4u

            memoryManager.i = 0x500u

            cpu.loadAllGeneralRegistersIntoMemory(0xF455u)

            expectThat(memoryManager.ram[0x500]).isEqualTo(0u)
            expectThat(memoryManager.ram[0x501]).isEqualTo(1u)
            expectThat(memoryManager.ram[0x502]).isEqualTo(2u)
            expectThat(memoryManager.ram[0x503]).isEqualTo(3u)
            expectThat(memoryManager.ram[0x504]).isEqualTo(4u)

            expectThat(memoryManager.i).isEqualTo(0x505u)
        } finally {
            FeatureManager.systemMode = SystemMode.SUPER_CHIP_MODE
        }
    }

    @Test
    fun `Load memory into general registers FX65`() {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        memoryManager.registers[0x0] = 9u
        memoryManager.registers[0x1] = 9u
        memoryManager.registers[0x2] = 9u
        memoryManager.registers[0x3] = 9u
        memoryManager.registers[0x4] = 9u
        memoryManager.registers[0x5] = 9u
        memoryManager.registers[0x6] = 9u
        memoryManager.registers[0x7] = 9u
        memoryManager.registers[0x8] = 9u
        memoryManager.registers[0x9] = 9u
        memoryManager.registers[0xA] = 9u

        memoryManager.i = 0x500u

        memoryManager.ram[0x500] = 10u
        memoryManager.ram[0x501] = 11u
        memoryManager.ram[0x502] = 12u
        memoryManager.ram[0x503] = 13u
        memoryManager.ram[0x504] = 14u
        memoryManager.ram[0x505] = 15u
        memoryManager.ram[0x506] = 16u
        memoryManager.ram[0x507] = 17u
        memoryManager.ram[0x508] = 18u
        memoryManager.ram[0x509] = 19u
        memoryManager.ram[0x50A] = 20u

        cpu.readMemoryIntoAllGeneralRegisters(0xF965u)

        expectThat(memoryManager.registers[0x0]).isEqualTo(10u)
        expectThat(memoryManager.registers[0x1]).isEqualTo(11u)
        expectThat(memoryManager.registers[0x2]).isEqualTo(12u)
        expectThat(memoryManager.registers[0x3]).isEqualTo(13u)
        expectThat(memoryManager.registers[0x4]).isEqualTo(14u)
        expectThat(memoryManager.registers[0x5]).isEqualTo(15u)
        expectThat(memoryManager.registers[0x6]).isEqualTo(16u)
        expectThat(memoryManager.registers[0x7]).isEqualTo(17u)
        expectThat(memoryManager.registers[0x8]).isEqualTo(18u)
        expectThat(memoryManager.registers[0x9]).isEqualTo(19u)
        expectThat(memoryManager.registers[0xA]).isEqualTo(9u)

        expectThat(memoryManager.i).isEqualTo(0x500u)
    }

    @ParameterizedTest
    @EnumSource(value = SystemMode::class, names = ["CHIP_8_MODE", "CHIP_48_MODE"])
    fun `Load memory into general registers in chip 8 or chip 48 mode FX65`(systemMode: SystemMode) {
        val memoryConfig =
            MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200)
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            memoryConfig
        )

        try {
            FeatureManager.systemMode = systemMode

            memoryManager.registers[0x0] = 9u
            memoryManager.registers[0x1] = 9u
            memoryManager.registers[0x2] = 9u
            memoryManager.registers[0x3] = 9u
            memoryManager.registers[0x4] = 9u
            memoryManager.registers[0x5] = 9u
            memoryManager.registers[0x6] = 9u
            memoryManager.registers[0x7] = 9u
            memoryManager.registers[0x8] = 9u
            memoryManager.registers[0x9] = 9u
            memoryManager.registers[0xA] = 9u

            memoryManager.i = 0x500u

            memoryManager.ram[0x500] = 10u
            memoryManager.ram[0x501] = 11u
            memoryManager.ram[0x502] = 12u
            memoryManager.ram[0x503] = 13u
            memoryManager.ram[0x504] = 14u
            memoryManager.ram[0x505] = 15u
            memoryManager.ram[0x506] = 16u
            memoryManager.ram[0x507] = 17u
            memoryManager.ram[0x508] = 18u
            memoryManager.ram[0x509] = 19u
            memoryManager.ram[0x50A] = 20u

            cpu.readMemoryIntoAllGeneralRegisters(0xF965u)

            expectThat(memoryManager.registers[0x0]).isEqualTo(10u)
            expectThat(memoryManager.registers[0x1]).isEqualTo(11u)
            expectThat(memoryManager.registers[0x2]).isEqualTo(12u)
            expectThat(memoryManager.registers[0x3]).isEqualTo(13u)
            expectThat(memoryManager.registers[0x4]).isEqualTo(14u)
            expectThat(memoryManager.registers[0x5]).isEqualTo(15u)
            expectThat(memoryManager.registers[0x6]).isEqualTo(16u)
            expectThat(memoryManager.registers[0x7]).isEqualTo(17u)
            expectThat(memoryManager.registers[0x8]).isEqualTo(18u)
            expectThat(memoryManager.registers[0x9]).isEqualTo(19u)
            expectThat(memoryManager.registers[0xA]).isEqualTo(9u)

            expectThat(memoryManager.i).isEqualTo(0x50Au)
        } finally {
            FeatureManager.systemMode = SystemMode.SUPER_CHIP_MODE
        }
    }

    private fun buildMemoryManager(memoryConfig: MemoryConfig): MemoryManager {
        return MemoryManager(soundRegister = mockk(), memoryConfig = memoryConfig)
    }
}