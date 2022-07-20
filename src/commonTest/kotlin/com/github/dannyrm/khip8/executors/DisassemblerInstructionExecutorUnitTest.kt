package com.github.dannyrm.khip8.executors

import io.mockk.MockKAnnotations
import io.mockk.impl.annotations.InjectMockKs
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.expect

class DisassemblerInstructionExecutorUnitTest {
    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
    }

    @InjectMockKs
    private lateinit var disassemblerInstructionExecutor: DissassemblerInstructionExecutor

    @AfterTest
    fun `After test`() {
        expect(1) { disassemblerInstructionExecutor.codeListing.size }
    }

    @Test
    fun `Sys call instruction`() {
        disassemblerInstructionExecutor.sysCall(0x0F6Du)
        expect("SYS F6D") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Clear screen instruction`() {
        disassemblerInstructionExecutor.clearScreen(0x00E0u)
        expect("CLS") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Return from subroutine instruction`() {
        disassemblerInstructionExecutor.doReturn(0x00EEu)
        expect("RET") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Jump instruction`() {
        disassemblerInstructionExecutor.jump(0x1EB6u)
        expect("JP EB6") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Call instruction`() {
        disassemblerInstructionExecutor.call(0x2EA6u)
        expect("CALL EA6") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Skip if register and memory equal instruction`() {
        disassemblerInstructionExecutor.skipIfRegisterAndMemoryEqual(0x3EB5u)
        expect("SE VE, B5") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Skip if register and memory not equal instruction`() {
        disassemblerInstructionExecutor.skipIfRegisterAndMemoryNotEqual(0x4DA2u)
        expect("SNE VD, A2") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Load memory into register instruction`() {
        disassemblerInstructionExecutor.loadMemoryIntoRegister(0x6FC3u)
        expect("LD VF, C3") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Add value to register instruction`() {
        disassemblerInstructionExecutor.addValueToRegister(0x7BD1u)
        expect("ADD VB, D1") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Load memory into I register instruction`() {
        disassemblerInstructionExecutor.loadMemoryIntoIRegister(0xA6A5u)
        expect("LD I, 6A5") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Jump with offset instruction`() {
        disassemblerInstructionExecutor.jumpWithOffset(0xB68Du)
        expect("JP V0, 68D") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Random instruction`() {
        disassemblerInstructionExecutor.random(0xCC5Au)
        expect("RND VC, 5A") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Draw instruction`() {
        disassemblerInstructionExecutor.draw(0xD4CFu)
        expect("DRW V4, VC, F") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Skip if register equals register instruction`() {
        disassemblerInstructionExecutor.skipIfRegisterAndRegisterEqual(0x5A30u)
        expect("SE VA, V3") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Load register into register instruction`() {
        disassemblerInstructionExecutor.loadRegisterIntoRegister(0x81A0u)
        expect("LD V1, VA") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Or instruction`() {
        disassemblerInstructionExecutor.or(0x82D1u)
        expect("OR V2, VD") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `And instruction`() {
        disassemblerInstructionExecutor.and(0x8E12u)
        expect("AND VE, V1") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Xor instruction`() {
        disassemblerInstructionExecutor.xor(0x80D3u)
        expect("XOR V0, VD") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Add register and register instruction`() {
        disassemblerInstructionExecutor.addRegisterAndRegister(0x8124u)
        expect("ADD V1, V2") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Subtract Y register from X register instruction`() {
        disassemblerInstructionExecutor.subtractYRegisterFromXRegister(0x8565u)
        expect("SUB V5, V6") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Shift right instruction`() {
        disassemblerInstructionExecutor.shiftRightXOnlyVariant(0x8986u)
        expect("SHR V9") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Shift left instruction`() {
        disassemblerInstructionExecutor.shiftLeftXOnlyVariant(0x852Eu)
        expect("SHL V5") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Subtract X register from Y register instruction`() {
        disassemblerInstructionExecutor.subtractXRegisterFromYRegister(0x8A37u)
        expect("SUBN VA, V3") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Skip if key pressed instruction`() {
        disassemblerInstructionExecutor.skipIfKeyPressed(0xE49Eu)
        expect("SKP V4") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Skip if key not pressed instruction`() {
        disassemblerInstructionExecutor.skipIfKeyNotPressed(0xE7A1u)
        expect("SKNP V7") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Set register to delay timer instruction`() {
        disassemblerInstructionExecutor.setRegisterToDelayTimerValue(0xFA07u)
        expect("LD VA, DT") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Wait for key press instruction`() {
        disassemblerInstructionExecutor.waitForKeyPress(0xF20Au)
        expect("LD V2, K") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Set delay timer to register instruction`() {
        disassemblerInstructionExecutor.setDelayTimerRegisterToValueInGeneralRegister(0xFD15u)
        expect("LD DT, VD") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Set sound timer to register instruction`() {
        disassemblerInstructionExecutor.setSoundTimerRegisterToValueInGeneralRegister(0xFE18u)
        expect("LD ST, VE") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Add general ledger to I register instruction`() {
        disassemblerInstructionExecutor.addGeneralRegisterToIRegister(0xF41Eu)
        expect("ADD I, V4") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Load location of sprite to I register instruction`() {
        disassemblerInstructionExecutor.loadIRegisterWithLocationOfSpriteForDigit(0xF829u)
        expect("LD F, V8") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Store BCD representation instruction`() {
        disassemblerInstructionExecutor.storeBCDRepresentation(0xF133u)
        expect("LD B, V1") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Load general registers into memory instruction`() {
        disassemblerInstructionExecutor.loadAllGeneralRegistersIntoMemory(0xFF55u)
        expect("LD [I], VF") { disassemblerInstructionExecutor.codeListing[0] }
    }

    @Test
    fun `Read memory into registers instruction`() {
        disassemblerInstructionExecutor.readMemoryIntoAllGeneralRegisters(0xF265u)
        expect("LD V2, [I]") { disassemblerInstructionExecutor.codeListing[0] }
    }
}