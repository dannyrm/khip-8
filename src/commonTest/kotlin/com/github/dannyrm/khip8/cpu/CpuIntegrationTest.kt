package com.github.dannyrm.khip8.cpu

import com.github.dannyrm.khip8.Khip8State.RUNNING
import com.github.dannyrm.khip8.config.MemoryConfig
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.test.utils.convertNumericParams
import com.github.dannyrm.khip8.util.FeatureManager
import com.github.dannyrm.khip8.util.SystemMode
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.mockk.mockk
import kotlin.test.expect

class CpuIntegrationTest: FunSpec({

    fun buildMemoryManager(memoryConfig: MemoryConfig): MemoryManager {
        return MemoryManager(memoryConfig = memoryConfig)
    }

    test("syscall works correctly") {
        val memoryConfig = memoryConfig()
        val memoryManager = buildMemoryManager(memoryConfig)

        // Jp to memory instruction
        memoryManager.ram[0x200] = 0x05u
        memoryManager.ram[0x201] = 0x55u

        val instructionDecoder = InstructionDecoder()

        val cpu = Cpu(
            instructionDecoder,
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryConfig,
            RUNNING
        )

        cpu.tick()

        expect(0x202u) { memoryManager.pc }
    }

    test("jump to memory location works correctly") {
        val memoryConfig = memoryConfig()
        val memoryManager = buildMemoryManager(memoryConfig)

        // Jp to memory instruction
        memoryManager.ram[0x200] = 0x15u
        memoryManager.ram[0x201] = 0x55u

        val instructionDecoder = InstructionDecoder()

        val cpu = Cpu(
            instructionDecoder,
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryConfig,
            RUNNING
        )

        cpu.tick()

        expect(0x555u) { memoryManager.pc }
    }

    test("Add value to register sequence 7XKK") {
        val memoryConfig = memoryConfig()
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryConfig,
            RUNNING
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

        expect(0x03.toUByte()) { memoryManager.registers[0x1] } // 01 + 01 + 01 = 03
        expect(0x10.toUByte()) { memoryManager.registers[0x5] }
        expect(0x09.toUByte()) { memoryManager.registers[0xF] } // 05 + 05 + FF = 09
        expect(0x01.toUByte()) { memoryManager.registers[0xE] }
    }

    test("Add value with wrapping 7XKK") {
        val memoryConfig = memoryConfig()
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryConfig,
            RUNNING
        )

        val instruction1 = 0x7503
        val addFFInstruction = 0x75FF

        cpu.addValueToRegister(instruction1.toUInt())

        expect(0x03.toUByte()) { memoryManager.registers[0x5] }

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expect(0x02.toUByte()) { memoryManager.registers[0x5] }

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expect(0x01.toUByte()) { memoryManager.registers[0x5] }

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expect(0x00.toUByte()) { memoryManager.registers[0x5] }

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expect(0xFF.toUByte()) { memoryManager.registers[0x5] }

        cpu.addValueToRegister(addFFInstruction.toUInt())

        expect(0xFE.toUByte()) { memoryManager.registers[0x5] }
    }

    test("Load register to register sequence 8XK0") {
        val memoryConfig = memoryConfig()
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryConfig,
            RUNNING
        )

        val instruction0 = 0x7510u
        val instruction01 = 0x79FFu

        val instruction1 = 0x8650u
        val instruction2 = 0x8F00u
        val instruction3 = 0x8F60u
        val instruction4 = 0x8FF0u
        val instruction5 = 0x8E90u

        cpu.addValueToRegister(instruction0)

        expect(0x10.toUByte()) { memoryManager.registers[0x5] }

        cpu.addValueToRegister(instruction01)

        expect(0xFF.toUByte()) { memoryManager.registers[0x9] }

        cpu.loadRegisterIntoRegister(instruction1)

        expect(0x10.toUByte()) { memoryManager.registers[0x6] }

        cpu.loadRegisterIntoRegister(instruction2)

        expect(0x00.toUByte()) { memoryManager.registers[0xF] }

        cpu.loadRegisterIntoRegister(instruction3)

        expect(0x10.toUByte()) { memoryManager.registers[0xF] }

        cpu.loadRegisterIntoRegister(instruction4)

        expect(0x10.toUByte()) { memoryManager.registers[0xF] }

        cpu.loadRegisterIntoRegister(instruction5)

        expect(0xFF.toUByte()) { memoryManager.registers[0xE] }
    }

    test("Load general registers into memory FX55") {
        val memoryConfig = memoryConfig()
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryConfig,
            RUNNING
        )

        memoryManager.registers[0x0] = 0u
        memoryManager.registers[0x1] = 1u
        memoryManager.registers[0x2] = 2u
        memoryManager.registers[0x3] = 3u
        memoryManager.registers[0x4] = 4u

        memoryManager.i = 0x500u

        cpu.loadAllGeneralRegistersIntoMemory(0xF455u)

        expect(0u) { memoryManager.ram[0x500] }
        expect(1u) { memoryManager.ram[0x501] }
        expect(2u) { memoryManager.ram[0x502] }
        expect(3u) { memoryManager.ram[0x503] }
        expect(4u) { memoryManager.ram[0x504] }

        expect(0x500u) { memoryManager.i }
    }

    test("Load memory into general registers FX65") {
        val memoryConfig = memoryConfig()
        val memoryManager = buildMemoryManager(memoryConfig)

        val cpu = Cpu(
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryManager,
            mockk(relaxed = true),
            mockk(relaxed = true),
            mockk(relaxed = true),
            memoryConfig,
            RUNNING
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

        expect(10u) { memoryManager.registers[0x0] }
        expect(11u) { memoryManager.registers[0x1] }
        expect(12u) { memoryManager.registers[0x2] }
        expect(13u) { memoryManager.registers[0x3] }
        expect(14u) { memoryManager.registers[0x4] }
        expect(15u) { memoryManager.registers[0x5] }
        expect(16u) { memoryManager.registers[0x6] }
        expect(17u) { memoryManager.registers[0x7] }
        expect(18u) { memoryManager.registers[0x8] }
        expect(19u) { memoryManager.registers[0x9] }
        expect(9u) { memoryManager.registers[0xA] }

        expect(0x500u) { memoryManager.i }
    }

    context("Get location of sprite digit FX29") {
        withData("F029,0,0,0","F129,1,1,5","F229,2,2,A","F329,3,3,F","F429,4,4,14","F529,5,5,19","F629,6,6,1E",
                 "F729,7,7,23","F829,8,8,28","F929,9,9,2D","FA29,A,A,32","FB29,B,B,37","FC29,C,C,3C",
                 "FD29,D,D,41","FE29,E,E,46","FF29,F,F,4B","F529,5,0,0","F529,5,F,4B") { input ->
            val (instruction: Int, registerNumber: Int, registerValue: Int, memoryLocation: Int) = convertNumericParams(
                input
            )

            val memoryConfig = memoryConfig()
            val memoryManager = buildMemoryManager(memoryConfig)

            val cpu = Cpu(
                mockk(relaxed = true),
                mockk(relaxed = true),
                memoryManager,
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                memoryConfig,
                RUNNING
            )

            memoryManager.registers[registerNumber] = registerValue.toUByte()

            cpu.loadIRegisterWithLocationOfSpriteForDigit(instruction.toUInt())

            expect(memoryLocation.toUInt()) { memoryManager.i }
        }
    }

    context("oad general registers into memory in Chip8 or Chip 48 Mode FX55") {
        withData("CHIP_8_MODE", "CHIP_48_MODE") { input ->
            val systemMode = SystemMode.valueOf(input)

            val memoryConfig = memoryConfig()
            val memoryManager = buildMemoryManager(memoryConfig)

            val cpu = Cpu(
                mockk(relaxed = true),
                mockk(relaxed = true),
                memoryManager,
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                memoryConfig,
                RUNNING
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

                expect(0u) { memoryManager.ram[0x500] }
                expect(1u) { memoryManager.ram[0x501] }
                expect(2u) { memoryManager.ram[0x502] }
                expect(3u) { memoryManager.ram[0x503] }
                expect(4u) { memoryManager.ram[0x504] }

                expect(0x505u) { memoryManager.i }
            } finally {
                FeatureManager.systemMode = SystemMode.SUPER_CHIP_MODE
            }
        }
    }

    context("Load memory into general registers in chip 8 or chip 48 mode FX65") {
        withData("CHIP_8_MODE", "CHIP_48_MODE") { input ->
            val systemMode = SystemMode.valueOf(input)

            val memoryConfig = memoryConfig()
            val memoryManager = buildMemoryManager(memoryConfig)

            val cpu = Cpu(
                mockk(relaxed = true),
                mockk(relaxed = true),
                memoryManager,
                mockk(relaxed = true),
                mockk(relaxed = true),
                mockk(relaxed = true),
                memoryConfig,
                RUNNING
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

                expect(10u) { memoryManager.registers[0x0] }
                expect(11u) { memoryManager.registers[0x1] }
                expect(12u) { memoryManager.registers[0x2] }
                expect(13u) { memoryManager.registers[0x3] }
                expect(14u) { memoryManager.registers[0x4] }
                expect(15u) { memoryManager.registers[0x5] }
                expect(16u) { memoryManager.registers[0x6] }
                expect(17u) { memoryManager.registers[0x7] }
                expect(18u) { memoryManager.registers[0x8] }
                expect(19u) { memoryManager.registers[0x9] }
                expect(9u) { memoryManager.registers[0xA] }

                expect(0x50Au) { memoryManager.i }
            } finally {
                FeatureManager.systemMode = SystemMode.SUPER_CHIP_MODE
            }
        }
    }
})

private fun memoryConfig() = MemoryConfig(memorySize = 4096, stackSize = 16, interpreterStartAddress = 0x0, programStartAddress = 0x200, numberOfGeneralPurposeRegisters = 16)
