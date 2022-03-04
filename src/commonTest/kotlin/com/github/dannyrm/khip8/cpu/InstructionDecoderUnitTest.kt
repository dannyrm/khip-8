package com.github.dannyrm.khip8.cpu

import com.github.dannyrm.khip8.executors.DissassemblerInstructionExecutor
import com.github.dannyrm.khip8.executors.InstructionExecutor
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertFailsWith

class InstructionDecoderUnitTest: FunSpec({
    lateinit var instructionExecutor: InstructionExecutor
    lateinit var instructionDecoder: InstructionDecoder

    beforeTest {
        instructionExecutor = mockk<InstructionExecutor>(relaxed = true)
        instructionDecoder = InstructionDecoder()
    }

    test("Works with multiple executors") {
        val dissassemblerInstructionExecutor = mockk<DissassemblerInstructionExecutor>(relaxed = true)

        instructionDecoder.decodeAndExecute(0x0237u, listOf(instructionExecutor, dissassemblerInstructionExecutor))
        verify { instructionExecutor.sysCall(0x0237u) }
        verify { dissassemblerInstructionExecutor.sysCall(0x0237u) }
    }

    test("Works with zero executors") {
        instructionDecoder.decodeAndExecute(0x0237u, listOf())
    }

    test("Decode Clear Screen instruction (CLS)") {
        instructionDecoder.decodeAndExecute(0x00E0u, listOf(instructionExecutor))
        verify { instructionExecutor.clearScreen(0x00E0u) }
    }

    test("Decode Subroutine return instruction (RET)") {
        instructionDecoder.decodeAndExecute(0x00EEu, listOf(instructionExecutor))
        verify { instructionExecutor.doReturn(0x00EEu) }
    }

    context("Decode Syscall instruction (SYS)") {
        withData(0x0123u, 0x0321u, 0x0456u) { input ->
            instructionDecoder.decodeAndExecute(input, listOf(instructionExecutor))
            verify { instructionExecutor.sysCall(input) }
        }
    }

    context("Decode Jump instruction (JP)") {
        withData(0x1123u, 0x1321u, 0x1456u) { input ->
            instructionDecoder.decodeAndExecute(input, listOf(instructionExecutor))
            verify { instructionExecutor.jump(input) }
        }
    }

    context("Decode Call instruction (CALL)") {
        withData(0x2123u, 0x2321u, 0x2456u) { input ->
            instructionDecoder.decodeAndExecute(input, listOf(instructionExecutor))
            verify { instructionExecutor.call(input) }
        }
    }

    context("Decode Skip if register equals memory instruction (SE Vx, byte)") {
        withData(0x3123u, 0x3321u, 0x3456u) { input ->
            instructionDecoder.decodeAndExecute(input, listOf(instructionExecutor))
            verify { instructionExecutor.skipIfRegisterAndMemoryEqual(input) }
        }
    }

    context("Decode Skip if register does not equal memory instruction (SNE Vx, byte)") {
        withData(0x4123, 0x4321, 0x4456) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.skipIfRegisterAndMemoryNotEqual(input.toUInt()) }
        }
    }

    context("Decode Skip if register equals register instruction (SE Vx, Vy)") {
        withData(0x5120, 0x5320, 0x5450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.skipIfRegisterAndRegisterEqual(input.toUInt()) }
        }
    }

    context("Decode Load memory into register instruction (LD Vx, byte)") {
        withData(0x6120, 0x6320, 0x6450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.loadMemoryIntoRegister(input.toUInt()) }
        }
    }

    context("Decode Add memory to register instruction (ADD Vx, byte)") {
        withData(0x7120, 0x7320, 0x7450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.addValueToRegister(input.toUInt()) }
        }
    }

    context("Decode Load register into register instruction (LD Vx, Vy)") {
        withData(0x8120, 0x8320, 0x8450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.loadRegisterIntoRegister(input.toUInt()) }
        }
    }

    context("Decode or instruction (OR Vx, Vy)") {
        withData(0x8121, 0x8321, 0x8451) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.or(input.toUInt()) }
        }
    }

    context("Decode and instruction (AND Vx, Vy)") {
        withData(0x8122, 0x8322, 0x8452) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.and(input.toUInt()) }
        }
    }

    context("Decode xor instruction (XOR Vx, Vy)") {
        withData(0x8123, 0x8323, 0x8453) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.xor(input.toUInt()) }
        }
    }

    context("Decode add register to register instruction (ADD Vx, Vy)") {
        withData(0x8124, 0x8324, 0x8454) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.addRegisterAndRegister(input.toUInt()) }
        }
    }

    context("Decode subtract y register from x register instruction (SUB Vx, Vy)") {
        withData(0x8125, 0x8325, 0x8455) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.subtractYRegisterFromXRegister(input.toUInt()) }
        }
    }

    context("Decode shift right instruction (SHR Vx {, Vy})") {
        withData(0x8126, 0x8326, 0x8456) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.shiftRightXOnlyVariant(input.toUInt()) }
        }
    }

    context("Decode shift left instruction (SHL Vx {, Vy})") {
        withData(0x812E, 0x832E, 0x845E) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.shiftLeftXOnlyVariant(input.toUInt()) }
        }
    }

    context("Decode subtract x register from y register instruction (SUBN Vx, Vy)") {
        withData(0x8127, 0x8327, 0x8457) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.subtractXRegisterFromYRegister(input.toUInt()) }
        }
    }

    context("Decode skip if registers are not equal instruction (SNE Vx, Vy)") {
        withData(0x9120, 0x9320, 0x9450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.skipIfRegisterAndRegisterNotEqual(input.toUInt()) }
        }
    }

    context("Decode load memory into I register instruction (LD I, addr)") {
        withData(0xA120, 0xA320, 0xA450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.loadMemoryIntoIRegister(input.toUInt()) }
        }
    }

    context("Decode jump with offset instruction (JP V0, addr)") {
        withData(0xB120, 0xB320, 0xB450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.jumpWithOffset(input.toUInt()) }            
        }
    }

    context("Decode random instruction (RND Vx, byte") {
        withData(0xC120, 0xC320, 0xC450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.random(input.toUInt()) }
        }
    }

    context("Decode draw instruction (DRW Vx, Vy, nibble)") {
        withData(0xD120, 0xD320, 0xD450) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.draw(input.toUInt()) }
        }
    }

    context("Decode skip if key is pressed instruction (SKP Vx)") {
        withData(0xE19E, 0xE59E, 0xE99E) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.skipIfKeyPressed(input.toUInt()) }
        }
    }


    context("Decode skip if key is not pressed instruction (SKNP Vx)") {
        withData(0xE1A1, 0xE5A1, 0xE9A1) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.skipIfKeyNotPressed(input.toUInt()) }
        }
    }

    context("Decode set value of Delay timer to register instruction (LD Vx, DT)") {
        withData(0xF107, 0xF307, 0xFF07) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.setRegisterToDelayTimerValue(input.toUInt()) }
        }
    }

    context("Decode wait for key press instruction (LD Vx, K)") {
        withData(0xF10A, 0xF30A, 0xFF0A) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.waitForKeyPress(input.toUInt()) }
        }
    }

    context("Decode set delay timer to register value instruction (LD DT, Vx)") {
        withData(0xF115, 0xF315, 0xFF15) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.setDelayTimerRegisterToValueInGeneralRegister(input.toUInt()) }
        }
    }

    context("Decode set sound timer to register value instruction (LD ST, Vx)") {
        withData(0xF118, 0xF318, 0xFF18) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.setSoundTimerRegisterToValueInGeneralRegister(input.toUInt()) }
        }
    }

    context("Decode add I register to general register value instruction (ADD I, Vx)") {
        withData(0xF11E, 0xF31E, 0xFF1E) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.addGeneralRegisterToIRegister(input.toUInt()) }
        }
    }

    context("Decode set I register to location of sprite for digit instruction (LD F, Vx)") {
        withData(0xF129, 0xF329, 0xFF29) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.loadIRegisterWithLocationOfSpriteForDigit(input.toUInt()) }
        }
    }

    context("Decode store BCD values of registers in memory locations instruction (LD B, Vx)") {
        withData(0xF133, 0xF333, 0xFF33) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.storeBCDRepresentation(input.toUInt()) }
        }
    }

    context("Decode store multiple register values into memory locations instruction (LD (I), Vx)") {
        withData(0xF155, 0xF355, 0xFF55) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.loadAllGeneralRegistersIntoMemory(input.toUInt()) }
        }
    }

    context("Decode store multiple memory values into register locations instruction (LD Vx, (I))") {
        withData(0xF165, 0xF365, 0xFF65) { input ->
            instructionDecoder.decodeAndExecute(input.toUInt(), listOf(instructionExecutor))
            verify { instructionExecutor.readMemoryIntoAllGeneralRegisters(input.toUInt()) }
        }
    }

    context("Decode unrecognised instructions") {
        withData(0x5121, 0x5122, 0x5123, 0x5124, 0x5125, 0x5126, 0x5127, 0x5128, 0x5129, // Requires the last byte to be zero (SE Vx, Vy)
                0x8128, 0x8129, 0x812A, 0x812B, 0x812C, 0x812D, 0x812F, // Unsupported 8 Prefix instructions
                0x9121, 0x9122, 0x9123, 0x9124, 0x9125, 0x9126, 0x9127, 0x9128, 0x9129, // Requires the last byte to be zero (SNE Vx, Vy)
                0xE11F, 0xE14F, 0xE1FF, 0xE1F1, 0xE1F4, 0xE1EF, 0xE1A2, 0xE1A5, // Some unsupported E Prefix instructions
                0xF108, 0xF106, 0xF10B, 0xF109, 0xF114, 0xF116, 0xF117, 0xF119, // Some unsupported F Prefix instructions
                0xF11D, 0xF11F, 0xF128, 0xF130, 0xF132, 0xF134, 0xF154, 0xF156, // Some more unsupported F Prefix instructions
                0xF164, 0xF166) { input -> // Some more unsupported F Prefix instructions
            assertFailsWith<IllegalArgumentException> {
                instructionDecoder.decodeAndExecute(
                        input.toUInt(),
                        listOf(instructionExecutor)
                )
            }
        }
    }
})
