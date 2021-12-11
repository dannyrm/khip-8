package uk.co.dmatthews.khip8.executors

import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThrows
import uk.co.dmatthews.khip8.cpu.Cpu

@ExtendWith(MockKExtension::class)
class CpuInstructionExecutorUnitTest {
    private lateinit var instructionExecutor: CpuInstructionExecutor

    @MockK(relaxed = true) private lateinit var cpu: Cpu

    @BeforeEach
    fun `Setup instruction executor`() {
        instructionExecutor = CpuInstructionExecutor()
        instructionExecutor.init(cpu)
    }

    @Test
    fun `Executor fails if Cpu not specified`() {
        instructionExecutor = CpuInstructionExecutor()
        expectThrows<UninitializedPropertyAccessException> { instructionExecutor.clearScreen(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Clear Screen instruction (CLS)`() {
        instructionExecutor.clearScreen(UNUSED_VALUE)
        verify { cpu.clearScreen(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Subroutine return instruction (RET)`() {
        instructionExecutor.doReturn(UNUSED_VALUE)
        verify { cpu.doReturn(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Syscall instruction (SYS)`() {
        instructionExecutor.sysCall(UNUSED_VALUE)
        verify { cpu.sysCall(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Jump instruction (JP)`() {
        instructionExecutor.jump(UNUSED_VALUE)
        verify { cpu.jump(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Call instruction (CALL)`() {
        instructionExecutor.call(UNUSED_VALUE)
        verify { cpu.call(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Skip if register equals memory instruction (SE Vx, byte)`() {
        instructionExecutor.skipIfRegisterAndMemoryEqual(UNUSED_VALUE)
        verify { cpu.skipIfRegisterAndMemoryEqual(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Skip if register does not equal memory instruction (SNE Vx, byte)`() {
        instructionExecutor.skipIfRegisterAndMemoryNotEqual(UNUSED_VALUE)
        verify { cpu.skipIfRegisterAndMemoryNotEqual(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Skip if register equals register instruction (SE Vx, Vy)`() {
        instructionExecutor.skipIfRegisterAndRegisterEqual(UNUSED_VALUE)
        verify { cpu.skipIfRegisterAndRegisterEqual(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Load memory into register instruction (LD Vx, byte)`() {
        instructionExecutor.loadMemoryIntoRegister(UNUSED_VALUE)
        verify { cpu.loadMemoryIntoRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Add memory to register instruction (ADD Vx, byte)`() {
        instructionExecutor.addValueToRegister(UNUSED_VALUE)
        verify { cpu.addValueToRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute Load register into register instruction (LD Vx, Vy)`() {
        instructionExecutor.loadRegisterIntoRegister(UNUSED_VALUE)
        verify { cpu.loadRegisterIntoRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute or instruction (OR Vx, Vy)`() {
        instructionExecutor.or(UNUSED_VALUE)
        verify { cpu.or(UNUSED_VALUE) }
    }

    @Test
    fun `Execute and instruction (AND Vx, Vy)`() {
        instructionExecutor.and(UNUSED_VALUE)
        verify { cpu.and(UNUSED_VALUE) }
    }

    @Test
    fun `Execute xor instruction (XOR Vx, Vy)`() {
        instructionExecutor.xor(UNUSED_VALUE)
        verify { cpu.xor(UNUSED_VALUE) }
    }

    @Test
    fun `Execute add register to register instruction (ADD Vx, Vy)`() {
        instructionExecutor.addRegisterAndRegister(UNUSED_VALUE)
        verify { cpu.addRegisterAndRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute subtract y register from x register instruction (SUB Vx, Vy)`() {
        instructionExecutor.subtractYRegisterFromXRegister(UNUSED_VALUE)
        verify { cpu.subtractYRegisterFromXRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute shift right instruction (SHR Vx {, Vy})`() {
        instructionExecutor.shiftRightXOnlyVariant(UNUSED_VALUE)
        verify { cpu.shiftRightXOnlyVariant(UNUSED_VALUE) }
    }

    @Test
    fun `Execute shift left instruction (SHL Vx {, Vy})`() {
        instructionExecutor.shiftLeftXOnlyVariant(UNUSED_VALUE)
        verify { cpu.shiftLeftXOnlyVariant(UNUSED_VALUE) }
    }

    @Test
    fun `Execute subtract x register from y register instruction (SUBN Vx, Vy)`() {
        instructionExecutor.subtractXRegisterFromYRegister(UNUSED_VALUE)
        verify { cpu.subtractXRegisterFromYRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute skip if registers are not equal instruction (SNE Vx, Vy)`() {
        instructionExecutor.skipIfRegisterAndRegisterNotEqual(UNUSED_VALUE)
        verify { cpu.skipIfRegisterAndRegisterNotEqual(UNUSED_VALUE) }
    }

    @Test
    fun `Execute load memory into I register instruction (LD I, addr)`() {
        instructionExecutor.loadMemoryIntoIRegister(UNUSED_VALUE)
        verify { cpu.loadMemoryIntoIRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute jump with offset instruction (JP V0, addr)`() {
        instructionExecutor.jumpWithOffset(UNUSED_VALUE)
        verify { cpu.jumpWithOffset(UNUSED_VALUE) }
    }

    @Test
    fun `Execute random instruction (RND Vx, byte)`() {
        instructionExecutor.random(UNUSED_VALUE)
        verify { cpu.random(UNUSED_VALUE) }
    }

    @Test
    fun `Execute draw instruction (DRW Vx, Vy, nibble)`() {
        instructionExecutor.draw(UNUSED_VALUE)
        verify { cpu.draw(UNUSED_VALUE) }
    }

    @Test
    fun `Execute skip if key is pressed instruction (SKP Vx)`() {
        instructionExecutor.skipIfKeyPressed(UNUSED_VALUE)
        verify { cpu.skipIfKeyPressed(UNUSED_VALUE) }
    }

    @Test
    fun `Execute skip if key is not pressed instruction (SKNP Vx)`() {
        instructionExecutor.skipIfKeyNotPressed(UNUSED_VALUE)
        verify { cpu.skipIfKeyNotPressed(UNUSED_VALUE) }
    }

    @Test
    fun `Execute set value of Delay timer to register instruction (LD Vx, DT)`() {
        instructionExecutor.setRegisterToDelayTimerValue(UNUSED_VALUE)
        verify { cpu.setRegisterToDelayTimerValue(UNUSED_VALUE) }
    }

    @Test
    fun `Execute wait for key press instruction (LD Vx, K)`() {
        instructionExecutor.waitForKeyPress(UNUSED_VALUE)
        verify { cpu.waitForKeyPress(UNUSED_VALUE) }
    }

    @Test
    fun `Execute set delay timer to register value instruction (LD DT, Vx)`() {
        instructionExecutor.setDelayTimerRegisterToValueInGeneralRegister(UNUSED_VALUE)
        verify { cpu.setDelayTimerRegisterToValueInGeneralRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute set sound timer to register value instruction (LD ST, Vx)`() {
        instructionExecutor.setSoundTimerRegisterToValueInGeneralRegister(UNUSED_VALUE)
        verify { cpu.setSoundTimerRegisterToValueInGeneralRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute add I register to general register value instruction (ADD I, Vx)`() {
        instructionExecutor.addGeneralRegisterToIRegister(UNUSED_VALUE)
        verify { cpu.addGeneralRegisterToIRegister(UNUSED_VALUE) }
    }

    @Test
    fun `Execute set I register to location of sprite for digit instruction (LD F, Vx)`() {
        instructionExecutor.loadIRegisterWithLocationOfSpriteForDigit(UNUSED_VALUE)
        verify { cpu.loadIRegisterWithLocationOfSpriteForDigit(UNUSED_VALUE) }
    }

    @Test
    fun `Execute store BCD values of registers in memory locations instruction (LD B, Vx)`() {
        instructionExecutor.storeBCDRepresentation(UNUSED_VALUE)
        verify { cpu.storeBCDRepresentation(UNUSED_VALUE) }
    }

    @Test
    fun `Execute store multiple register values into memory locations instruction (LD (I), Vx)`() {
        instructionExecutor.loadAllGeneralRegistersIntoMemory(UNUSED_VALUE)
        verify { cpu.loadAllGeneralRegistersIntoMemory(UNUSED_VALUE) }
    }

    @Test
    fun `Execute store multiple memory values into register locations instruction (LD Vx, (I))`() {
        instructionExecutor.readMemoryIntoAllGeneralRegisters(UNUSED_VALUE)
        verify { cpu.readMemoryIntoAllGeneralRegisters(UNUSED_VALUE) }
    }

    companion object {
        const val UNUSED_VALUE = 54u
    }
}