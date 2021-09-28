package uk.co.dmatthews.khip8.cpu

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.ValueSource
import strikt.api.expectThat
import strikt.api.expectThrows
import strikt.assertions.isEqualTo
import java.lang.IllegalArgumentException

@ExtendWith(MockKExtension::class)
class InstructionDecoderUnitTest {
    @MockK
    private lateinit var cpu: Cpu
    
    @InjectMockKs
    private lateinit var instructionDecoder: InstructionDecoder

    @Test
    fun `Decode Clear Screen instruction (CLS)`() {
        expectThat(instructionDecoder.decode(0x00E0u)).isEqualTo(cpu::clearScreen)
    }

    @Test
    fun `Decode Subroutine return instruction (RET)`() {
        expectThat(instructionDecoder.decode(0x00EEu)).isEqualTo(cpu::doReturn)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x0123, 0x0321, 0x0456 ])
    fun `Decode Syscall instruction (SYS)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::sysCall)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x1123, 0x1321, 0x1456 ])
    fun `Decode Jump instruction (JP)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::jump)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x2123, 0x2321, 0x2456 ])
    fun `Decode Call instruction (CALL)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::call)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x3123, 0x3321, 0x3456 ])
    fun `Decode Skip if register equals memory instruction (SE Vx, byte)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::skipIfRegisterAndMemoryEqual)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x4123, 0x4321, 0x4456 ])
    fun `Decode Skip if register does not equal memory instruction (SNE Vx, byte)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::skipIfRegisterAndMemoryNotEqual)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x5120, 0x5320, 0x5450 ])
    fun `Decode Skip if register equals register instruction (SE Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::skipIfRegisterAndRegisterEqual)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x6120, 0x6320, 0x6450 ])
    fun `Decode Load memory into register instruction (LD Vx, byte)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::loadMemoryIntoRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x7120, 0x7320, 0x7450 ])
    fun `Decode Add memory to register instruction (ADD Vx, byte)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::addValueToRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x8120, 0x8320, 0x8450 ])
    fun `Decode Load register into register instruction (LD Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::loadRegisterIntoRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x8121, 0x8321, 0x8451 ])
    fun `Decode or instruction (OR Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::or)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x8122, 0x8322, 0x8452 ])
    fun `Decode and instruction (AND Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::and)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x8123, 0x8323, 0x8453 ])
    fun `Decode xor instruction (XOR Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::xor)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x8124, 0x8324, 0x8454 ])
    fun `Decode add register to register instruction (ADD Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::addRegisterAndRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x8125, 0x8325, 0x8455 ])
    fun `Decode subtract y register from x register instruction (SUB Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::subtractYRegisterFromXRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x8126, 0x8326, 0x8456 ])
    fun `Decode shift right instruction (SHR Vx {, Vy})`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::shiftRight)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x812E, 0x832E, 0x845E ])
    fun `Decode shift left instruction (SHL Vx {, Vy})`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::shiftLeft)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x8127, 0x8327, 0x8457 ])
    fun `Decode subtract x register from y register instruction (SUBN Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::subtractXRegisterFromYRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0x9120, 0x9320, 0x9450 ])
    fun `Decode skip if registers are not equal instruction (SNE Vx, Vy)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::skipIfRegisterAndRegisterNotEqual)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xA120, 0xA320, 0xA450 ])
    fun `Decode load memory into I register instruction (LD I, addr)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::loadMemoryIntoIRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xB120, 0xB320, 0xB450 ])
    fun `Decode jump with offset instruction (JP V0, addr)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::jumpWithOffset)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xC120, 0xC320, 0xC450 ])
    fun `Decode random instruction (RND Vx, byte)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::random)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xD120, 0xD320, 0xD450 ])
    fun `Decode draw instruction (DRW Vx, Vy, nibble)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::draw)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xE19E, 0xE59E, 0xE99E ])
    fun `Decode skip if key is pressed instruction (SKP Vx)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::skipIfKeyPressed)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xE1A1, 0xE5A1, 0xE9A1 ])
    fun `Decode skip if key is not pressed instruction (SKNP Vx)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::skipIfKeyNotPressed)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF107, 0xF307, 0xFF07 ])
    fun `Decode set value of Delay timer to register instruction (LD Vx, DT)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::setRegisterToDelayTimerValue)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF10A, 0xF30A, 0xFF0A ])
    fun `Decode wait for key press instruction (LD Vx, K)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::waitForKeyPress)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF115, 0xF315, 0xFF15 ])
    fun `Decode set delay timer to register value instruction (LD DT, Vx)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::setDelayTimerRegisterToValueInGeneralRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF118, 0xF318, 0xFF18 ])
    fun `Decode set sound timer to register value instruction (LD ST, Vx)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::setSoundTimerRegisterToValueInGeneralRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF11E, 0xF31E, 0xFF1E ])
    fun `Decode add I register to general register value instruction (ADD I, Vx)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::addIRegisterToGeneralRegister)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF129, 0xF329, 0xFF29 ])
    fun `Decode set I register to location of sprite for digit instruction (LD F, Vx)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::loadIRegisterWithLocationOfSpriteForDigit)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF133, 0xF333, 0xFF33 ])
    fun `Decode store BCD values of registers in memory locations instruction (LD B, Vx)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::storeBCDRepresentation)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF155, 0xF355, 0xFF55 ])
    fun `Decode store multiple register values into memory locations instruction (LD (I), Vx)`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::loadAllGeneralRegistersIntoMemory)
    }

    @ParameterizedTest
    @ValueSource(ints = [ 0xF165, 0xF365, 0xFF65 ])
    fun `Decode store multiple memory values into register locations instruction (LD Vx, (I))`(param: Int) {
        expectThat(instructionDecoder.decode(param.toUInt())).isEqualTo(cpu::readMemoryIntoAllGeneralRegisters)
    }

    @ParameterizedTest
    @ValueSource(ints = [
        0x5123, // Requires the last byte to be zero (SE Vx, Vy)
        0x8128, 0x8129, 0x812A, 0x812B, 0x812C, 0x812D, 0x812F, // Unsupported 8 Prefix instructions
        0x9123, // Requires the last byte to be zero (SNE Vx, Vy)
        0xE11F, 0xE14F, 0xE1FF, 0xE1F1, 0xE1F4, 0xE1EF, 0xE1A2, 0xE1A5, // Some unsupported E Prefix instructions
        0xF108, 0xF106, 0xF10B, 0xF109, 0xF114, 0xF116, 0xF117, 0xF119, // Some unsupported F Prefix instructions
        0xF11D, 0xF11F, 0xF128, 0xF130, 0xF132, 0xF134, 0xF154, 0xF156, // Some more unsupported F Prefix instructions
        0xF164, 0xF166, // Some more unsupported F Prefix instructions
    ])
    fun `Decode unrecognised instructions`(param: Int) {
        expectThrows<IllegalArgumentException> { instructionDecoder.decode(param.toUInt()) }
    }
}