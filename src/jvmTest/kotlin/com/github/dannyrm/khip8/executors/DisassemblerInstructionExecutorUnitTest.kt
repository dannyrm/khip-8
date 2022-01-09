package com.github.dannyrm.khip8.executors

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.test.AfterTest

@ExtendWith(MockKExtension::class)
class DisassemblerInstructionExecutorUnitTest {
    @InjectMockKs
    private lateinit var disassemblerInstructionExecutor: DissassemblerInstructionExecutor

    @AfterTest
    fun `After test`() {
        expectThat(disassemblerInstructionExecutor.codeListing.size).isEqualTo(1)
    }

    @Test
    fun `Sys call instruction`() {
        disassemblerInstructionExecutor.sysCall(0x0F6Du)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SYS F6D")
    }

    @Test
    fun `Clear screen instruction`() {
        disassemblerInstructionExecutor.clearScreen(0x00E0u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("CLS")
    }

    @Test
    fun `Return from subroutine instruction`() {
        disassemblerInstructionExecutor.doReturn(0x00EEu)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("RET")
    }

    @Test
    fun `Jump instruction`() {
        disassemblerInstructionExecutor.jump(0x1EB6u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("JP EB6")
    }

    @Test
    fun `Call instruction`() {
        disassemblerInstructionExecutor.call(0x2EA6u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("CALL EA6")
    }

    @Test
    fun `Skip if register and memory equal instruction`() {
        disassemblerInstructionExecutor.skipIfRegisterAndMemoryEqual(0x3EB5u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SE VE, B5")
    }

    @Test
    fun `Skip if register and memory not equal instruction`() {
        disassemblerInstructionExecutor.skipIfRegisterAndMemoryNotEqual(0x4DA2u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SNE VD, A2")
    }

    @Test
    fun `Load memory into register instruction`() {
        disassemblerInstructionExecutor.loadMemoryIntoRegister(0x6FC3u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD VF, C3")
    }

    @Test
    fun `Add value to register instruction`() {
        disassemblerInstructionExecutor.addValueToRegister(0x7BD1u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("ADD VB, D1")
    }

    @Test
    fun `Load memory into I register instruction`() {
        disassemblerInstructionExecutor.loadMemoryIntoIRegister(0xA6A5u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD I, 6A5")
    }

    @Test
    fun `Jump with offset instruction`() {
        disassemblerInstructionExecutor.jumpWithOffset(0xB68Du)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("JP V0, 68D")
    }

    @Test
    fun `Random instruction`() {
        disassemblerInstructionExecutor.random(0xCC5Au)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("RND VC, 5A")
    }

    @Test
    fun `Draw instruction`() {
        disassemblerInstructionExecutor.draw(0xD4CFu)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("DRW V4, VC, F")
    }

    @Test
    fun `Skip if register equals register instruction`() {
        disassemblerInstructionExecutor.skipIfRegisterAndRegisterEqual(0x5A30u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SE VA, V3")
    }

    @Test
    fun `Load register into register instruction`() {
        disassemblerInstructionExecutor.loadRegisterIntoRegister(0x81A0u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD V1, VA")
    }

    @Test
    fun `Or instruction`() {
        disassemblerInstructionExecutor.or(0x82D1u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("OR V2, VD")
    }

    @Test
    fun `And instruction`() {
        disassemblerInstructionExecutor.and(0x8E12u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("AND VE, V1")
    }

    @Test
    fun `Xor instruction`() {
        disassemblerInstructionExecutor.xor(0x80D3u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("XOR V0, VD")
    }

    @Test
    fun `Add register and register instruction`() {
        disassemblerInstructionExecutor.addRegisterAndRegister(0x8124u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("ADD V1, V2")
    }

    @Test
    fun `Subtract Y register from X register instruction`() {
        disassemblerInstructionExecutor.subtractYRegisterFromXRegister(0x8565u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SUB V5, V6")
    }

    @Test
    fun `Shift right instruction`() {
        disassemblerInstructionExecutor.shiftRightXOnlyVariant(0x8986u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SHR V9")
    }

    @Test
    fun `Shift left instruction`() {
        disassemblerInstructionExecutor.shiftLeftXOnlyVariant(0x852Eu)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SHL V5")
    }

    @Test
    fun `Subtract X register from Y register instruction`() {
        disassemblerInstructionExecutor.subtractXRegisterFromYRegister(0x8A37u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SUBN VA, V3")
    }

    @Test
    fun `Skip if key pressed instruction`() {
        disassemblerInstructionExecutor.skipIfKeyPressed(0xE49Eu)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SKP V4")
    }

    @Test
    fun `Skip if key not pressed instruction`() {
        disassemblerInstructionExecutor.skipIfKeyNotPressed(0xE7A1u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("SKNP V7")
    }

    @Test
    fun `Set register to delay timer instruction`() {
        disassemblerInstructionExecutor.setRegisterToDelayTimerValue(0xFA07u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD VA, DT")
    }

    @Test
    fun `Wait for key press instruction`() {
        disassemblerInstructionExecutor.waitForKeyPress(0xF20Au)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD V2, K")
    }

    @Test
    fun `Set delay timer to register instruction`() {
        disassemblerInstructionExecutor.setDelayTimerRegisterToValueInGeneralRegister(0xFD15u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD DT, VD")
    }

    @Test
    fun `Set sound timer to register instruction`() {
        disassemblerInstructionExecutor.setSoundTimerRegisterToValueInGeneralRegister(0xFE18u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD ST, VE")
    }

    @Test
    fun `Add general ledger to I register instruction`() {
        disassemblerInstructionExecutor.addGeneralRegisterToIRegister(0xF41Eu)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("ADD I, V4")
    }

    @Test
    fun `Load location of sprite to I register instruction`() {
        disassemblerInstructionExecutor.loadIRegisterWithLocationOfSpriteForDigit(0xF829u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD F, V8")
    }

    @Test
    fun `Store BCD representation instruction`() {
        disassemblerInstructionExecutor.storeBCDRepresentation(0xF133u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD B, V1")
    }

    @Test
    fun `Load general registers into memory instruction`() {
        disassemblerInstructionExecutor.loadAllGeneralRegistersIntoMemory(0xFF55u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD [I], VF")
    }

    @Test
    fun `Read memory into registers instruction`() {
        disassemblerInstructionExecutor.readMemoryIntoAllGeneralRegisters(0xF265u)
        expectThat(disassemblerInstructionExecutor.codeListing[0]).isEqualTo("LD V2, [I]")
    }
}