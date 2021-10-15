package uk.co.dmatthews.khip8.cpu

import io.mockk.mockk
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import uk.co.dmatthews.khip8.memory.MemoryManager

class CpuIntegrationTest {

    @Test
    fun `Add value to register sequence 7XKK`() {
        val memoryManager = MemoryManager()
        val cpu = Cpu(mockk(relaxed = true), mockk(relaxed = true), memoryManager, mockk(relaxed = true))

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
        val memoryManager = MemoryManager()
        val cpu = Cpu(mockk(relaxed = true), mockk(relaxed = true), memoryManager, mockk(relaxed = true))

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
        val memoryManager = MemoryManager()
        val cpu = Cpu(mockk(relaxed = true), mockk(relaxed = true), memoryManager, mockk(relaxed = true))

        val instruction0 = 0x7510
        val instruction01 = 0x79FF

        val instruction1 = 0x8650
        val instruction2 = 0x8F00
        val instruction3 = 0x8F60
        val instruction4 = 0x8FF0
        val instruction5 = 0x8E90

        cpu.addValueToRegister(instruction0.toUInt())

        expectThat(memoryManager.registers[0x5]).isEqualTo(0x10.toUByte())

        cpu.addValueToRegister(instruction01.toUInt())

        expectThat(memoryManager.registers[0x9]).isEqualTo(0xFF.toUByte())

        cpu.loadRegisterIntoRegister(instruction1.toUInt())

        expectThat(memoryManager.registers[0x6]).isEqualTo(0x10.toUByte())

        cpu.loadRegisterIntoRegister(instruction2.toUInt())

        expectThat(memoryManager.registers[0xF]).isEqualTo(0x00.toUByte())

        cpu.loadRegisterIntoRegister(instruction3.toUInt())

        expectThat(memoryManager.registers[0xF]).isEqualTo(0x10.toUByte())

        cpu.loadRegisterIntoRegister(instruction4.toUInt())

        expectThat(memoryManager.registers[0xF]).isEqualTo(0x10.toUByte())

        cpu.loadRegisterIntoRegister(instruction5.toUInt())

        expectThat(memoryManager.registers[0xE]).isEqualTo(0xFF.toUByte())
    }
}