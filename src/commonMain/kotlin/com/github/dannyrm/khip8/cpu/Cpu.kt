package com.github.dannyrm.khip8.cpu

import com.github.dannyrm.khip8.Khip8State
import com.github.dannyrm.khip8.Khip8State.PAUSED
import com.github.dannyrm.khip8.Khip8State.RUNNING
import com.github.dannyrm.khip8.config.ConfigManager
import nibbleByteHex
import rightByte
import rightNibble
import rightNibbleByte
import toHex
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.executors.CpuInstructionExecutor
import com.github.dannyrm.khip8.input.InputManager
import com.github.dannyrm.khip8.logger
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.github.dannyrm.khip8.util.FeatureManager
import com.github.dannyrm.khip8.util.SystemDependentInstructionFeature
import org.koin.core.annotation.Single
import wordHex
import x
import y

@Single
class Cpu(private val instructionDecoder: InstructionDecoder,
          private val displayMemory: DisplayMemory,
          private val memoryManager: MemoryManager,
          private val delayRegister: TimerRegister,
          private val soundRegister: SoundTimerRegister,
          private val inputManager: InputManager,
          private val memorySize: Int,
          var cpuState: Khip8State) {

    fun tick() {
        // Lock inputs, so they can't change during the cycle.
        inputManager.lockInputs()

        if (cpuState == RUNNING) {
            // FETCH
            val instruction = memoryManager.fetchNextInstruction()

            // DECODE & EXECUTE
            instructionDecoder.decodeAndExecute(instruction)
        }
    }

    /**
     * 0nnn - SYS addr
     * Jump to a machine code routine at nnn.
     * This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
     */
    fun sysCall(value: UInt) {
        LOG.debug { "SYS ${rightNibbleByte(value)}" }
    }

    /**
     * 00E0 - CLS
     * Clear the display.
     */
    fun clearScreen(@Suppress("UNUSED_PARAMETER") unusedValue: UInt) {
        displayMemory.clear()
        LOG.debug { "CLS" }
    }

    /**
     * 00EE - RET
     * Sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
     */
    fun doReturn(@Suppress("UNUSED_PARAMETER") unusedValue: UInt) {
        memoryManager.pc = memoryManager.stack.pop()

        LOG.debug { "RET" }
    }

    /**
     * 1nnn - JP
     * Jump to location nnn - The interpreter sets the program counter to nnn.
     */
    fun jump(value: UInt) {
        val nnn = rightNibbleByte(value)

        memoryManager.pc = nnn

        LOG.debug { "JP ${nibbleByteHex(nnn)}" }
    }

    /**
     * 2nnn - CALL
     * Call subroutine at nnn.
     * Increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
     */
    fun call(value: UInt) {
        val nnn = rightNibbleByte(value)

        memoryManager.stack.push(memoryManager.pc)
        memoryManager.pc = nnn

        LOG.debug { "CALL ${nibbleByteHex(nnn)}" }
    }

    /**
     * 3xkk - SE Vx, byte
     * Skip next instruction if Vx = kk.
     * If equal increment the program counter by 2.
     */
    fun skipIfRegisterAndMemoryEqual(value: UInt) {
        val x = x(value)
        val byte = rightByte(value)

        if (memoryManager.registers[x.toInt()] == byte) {
            memoryManager.skipNextInstruction()
        }

        LOG.debug { "SE V${toHex(x)}, ${toHex(byte)}" }
    }

    /**
     * 4xkk - SNE Vx, byte
     * Skip next instruction if Vx != kk.
     * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
     */
    fun skipIfRegisterAndMemoryNotEqual(value: UInt) {
        val x = x(value)
        val byte = rightByte(value)

        if (memoryManager.registers[x.toInt()] != byte) {
            memoryManager.skipNextInstruction()
        }

        LOG.debug { "SNE V${toHex(x)}, ${toHex(byte)}" }
    }

    /**
     * 5xy0 - SE Vx, Vy
     * Skip next instruction if Vx = Vy.
     * The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
     */
    fun skipIfRegisterAndRegisterEqual(value: UInt) {
        val x = x(value)
        val y = y(value)

        if (memoryManager.registers[x.toInt()] == memoryManager.registers[y.toInt()]) {
            memoryManager.skipNextInstruction()
        }

        LOG.debug { "SE V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 6xkk - LD Vx, byte
     * Set Vx = kk.
     * The interpreter puts the value kk into register Vx.
     */
    fun loadMemoryIntoRegister(value: UInt) {
        val x = x(value)
        val byte = rightByte(value)

        memoryManager.registers[x.toInt()] = byte

        LOG.debug { "LD V${toHex(x)}, ${toHex(byte)}" }
    }

    /**
     * 7xkk - ADD Vx, byte
     * Set Vx = Vx + kk.
     * Adds the value kk to the value of register Vx, then stores the result in Vx.
     * If the final value exceeds decimal 0xFF, the register will wraparound to a corresponding value that can be
     * stored. In other words, the register will always be reduced modulo decimal 256.
     * See https://github.com/mattmikolay/chip-8/wiki/Mastering-CHIP%E2%80%908#chip-8-instructions
     * */
    fun addValueToRegister(value: UInt) {
        val x = x(value)
        val byte = rightByte(value)

        memoryManager.registers[x.toInt()] = (memoryManager.registers[x.toInt()] + byte).toUByte()

        LOG.debug { "ADD V${toHex(x)}, ${toHex(byte)}" }
    }

    /**
     * 8xy0 - LD Vx, Vy
     * Set Vx = Vy.
     * Stores the value of register Vy in register Vx.
     */
    fun loadRegisterIntoRegister(value: UInt) {
        val x = x(value)
        val y = y(value)

        memoryManager.registers[x.toInt()] = memoryManager.registers[y.toInt()]

        LOG.debug { "LD V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 8xy1 - OR Vx, Vy
     * Set Vx = Vx OR Vy.
     * Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx.
     */
    fun or(value: UInt) {
        val x = x(value)
        val y = y(value)

        memoryManager.registers[x.toInt()] = memoryManager.registers[x.toInt()] or memoryManager.registers[y.toInt()]

        LOG.debug { "OR V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 8xy2 - AND Vx, Vy
     * Set Vx = Vx AND Vy.
     * Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx.
     */
    fun and(value: UInt) {
        val x = x(value)
        val y = y(value)

        memoryManager.registers[x.toInt()] = memoryManager.registers[x.toInt()] and memoryManager.registers[y.toInt()]

        LOG.debug { "AND V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 8xy3 - XOR Vx, Vy
     * Set Vx = Vx XOR Vy.
     * Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx.
     */
    fun xor(value: UInt) {
        val x = x(value)
        val y = y(value)

        memoryManager.registers[x.toInt()] = memoryManager.registers[x.toInt()] xor memoryManager.registers[y.toInt()]

        LOG.debug { "XOR V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 8xy4 - ADD Vx, Vy
     * Set Vx = Vx + Vy, set VF = carry.
     * The values of Vx and Vy are added together.
     * If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0.
     * Only the lowest 8 bits of the result are kept, and stored in Vx.
     */
    fun addRegisterAndRegister(value: UInt) {
        val x = x(value)
        val y = y(value)

        val xValue = memoryManager.registers[x.toInt()]
        val yValue = memoryManager.registers[y.toInt()]

        val result = xValue.toUInt() + yValue.toUInt()

        memoryManager.registers[0xF] = if (result > 255u) 0x1u else 0x0u
        memoryManager.registers[x.toInt()] = result.toUByte()

        LOG.debug { "ADD V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 8xy5 - SUB Vx, Vy
     * Set Vx = Vx - Vy, set VF = NOT borrow.
     * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
     */
    fun subtractYRegisterFromXRegister(value: UInt) {
        val x = x(value)
        val y = y(value)

        val xValue = memoryManager.registers[x.toInt()]
        val yValue = memoryManager.registers[y.toInt()]

        memoryManager.registers[0xF] = if (xValue > yValue) 0x1u else 0x0u
        memoryManager.registers[x.toInt()] = (xValue.toUInt() - yValue.toUInt()).toUByte()

        LOG.debug { "SUB V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 8xy6 - SHR Vx {, Vy}
     * Store the value of register VY shifted right one bit in register VX
     * Set register VF to the least significant bit prior to the shift
     * See https://github.com/mattmikolay/chip-8/wiki/Mastering-CHIP%E2%80%908#chip-8-instructions
     * TODO: Do we need this anymore?
     */
    fun shiftRight(value: UInt) {
        val x = x(value)
        val y = y(value)

        val yValue = memoryManager.registers[y.toInt()]
        memoryManager.registers[0xF] = yValue and 0x1u
        memoryManager.registers[x.toInt()] = (yValue.toUInt() shr 1).toUByte()

        LOG.debug { "SHR V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 8xy6
     * Set VX equal to VX bitshifted right 1.
     * VF is set to the least significant bit of VX prior to the shift.
     * Originally this opcode meant set VX equal to VY bitshifted right 1 but emulators and software seem to ignore
     * VY now.
     * Note: This instruction was originally undocumented but functional due to how the 8XXX instructions were
     * implemented on the COSMAC VIP.
     * See https://github.com/trapexit/chip-8_documentation
     * // TODO: Should the original version be handled in Chip 8 mode and in the disassembler?
     */
    fun shiftRightXOnlyVariant(value: UInt) {
        val x = x(value)

        val xValue = memoryManager.registers[x.toInt()]
        memoryManager.registers[0xF] = xValue and 0x1u
        memoryManager.registers[x.toInt()] = (xValue.toUInt() shr 1).toUByte()

        LOG.debug { "SHR V${toHex(x)}" }
    }

    /**
     * 8xy7 - SUBN Vx, Vy
     * Set Vx = Vy - Vx, set VF = NOT borrow.
     * If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
     */
    fun subtractXRegisterFromYRegister(value: UInt) {
        val x = x(value)
        val y = y(value)

        val xValue = memoryManager.registers[x.toInt()]
        val yValue = memoryManager.registers[y.toInt()]

        memoryManager.registers[0xF] = if (yValue > xValue) 0x1u else 0x0u
        memoryManager.registers[x.toInt()] = (yValue.toUInt() - xValue.toUInt()).toUByte()

        LOG.debug { "SUBN V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * 8xyE - SHL Vx {, Vy}
     * Store the value of register VY shifted left one bit in register VX
     * Set register VF to the most significant bit prior to the shift
     * See https://github.com/mattmikolay/chip-8/wiki/Mastering-CHIP%E2%80%908#chip-8-instructions
     * TODO: Do we need this anymore?
     */
    fun shiftLeft(value: UInt) {
        val x = x(value)
        val y = y(value)

        val yValue = memoryManager.registers[y.toInt()]
        memoryManager.registers[0xF] = ((yValue and 0x80u).toUInt() shr 7).toUByte()
        memoryManager.registers[x.toInt()] = (yValue.toUInt() shl 0x1).toUByte()

        LOG.debug { "SHL V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * Set VX equal to VX bitshifted left 1. VF is set to the most significant bit of VX prior to the shift.
     * Originally this opcode meant set VX equal to VY bitshifted left 1 but emulators and software seem to ignore
     * VY now.
     * Note: This instruction was originally undocumented but functional due to how the 8XXX instructions were
     * implemented on the COSMAC VIP.
     * See https://github.com/trapexit/chip-8_documentation
     * // TODO: Should the original version be handled in Chip 8 mode and in the disassembler?
     */
    fun shiftLeftXOnlyVariant(value: UInt) {
        val x = x(value)

        val xValue = memoryManager.registers[x.toInt()]
        memoryManager.registers[0xF] = ((xValue and 0x80u).toUInt() shr 7).toUByte()
        memoryManager.registers[x.toInt()] = (xValue.toUInt() shl 0x1).toUByte()

        LOG.debug { "SHL V${toHex(x)}" }
    }

    /**
     * 9xy0 - SNE Vx, Vy
     * Skip next instruction if Vx != Vy.
     * The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
     */
    fun skipIfRegisterAndRegisterNotEqual(value: UInt) {
        val x = x(value)
        val y = y(value)

        if (memoryManager.registers[x.toInt()] != memoryManager.registers[y.toInt()]) {
            memoryManager.skipNextInstruction()
        }

        LOG.debug { "SNE V${toHex(x)}, V${toHex(y)}" }
    }

    /**
     * Annn - LD I, addr
     * Set I = nnn.
     * The value of register I is set to nnn.
     */
    fun loadMemoryIntoIRegister(instruction: UInt) {
        val value = rightNibbleByte(instruction)

        memoryManager.i = value
        LOG.debug { "LD I, ${wordHex(value)}" }
    }

    /**
     * Bnnn - JP V0, addr
     * Jump to location nnn + V0.
     * The program counter is set to nnn plus the value of V0.
     */
    fun jumpWithOffset(instruction: UInt) {
        val value = rightNibbleByte(instruction)

        memoryManager.pc = value + memoryManager.registers[0].toUInt()

        LOG.debug { "JP V0, ${wordHex(value)}" }
    }

    /**
     * Cxkk - RND Vx, byte
     * Set Vx = random byte AND kk.
     * The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
     * The results are stored in Vx.
     */
    fun random(value: UInt) {
        val x = x(value)
        val byte = rightByte(value)

        val randomValue = (0x0..0xFF).random().toUByte()
        val randomValueMasked = randomValue and byte

        memoryManager.registers[x.toInt()] = randomValueMasked

        LOG.debug { "RND V${toHex(x)}, ${toHex(byte)}" }
    }

    /**
     * Dxyn - DRW Vx, Vy, nibble
     * Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
     * The interpreter reads n bytes from memory, starting at the address stored in I.
     * These bytes are then displayed as sprites on screen at coordinates (Vx, Vy).
     * Sprites are XORed onto the existing screen.
     * If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0.
     * If the sprite is positioned so part of it is outside the coordinates of the display,
     * it wraps around to the opposite side of the screen.
     * TODO Is the wrapping behaviour right? Other docs say otherwise
     */
    fun draw(value: UInt) {
        val x = x(value)
        val y = y(value)

        val xValue = memoryManager.registers[x.toInt()].toInt()
        val yValue = memoryManager.registers[y.toInt()].toInt()

        val spriteHeight = rightNibble(value).toInt()
        val startingAddress = memoryManager.i

        var collisionValue = 0u

        for (i in 0 until spriteHeight) {
            displayMemory[xValue, yValue+i] = memoryManager.ram[startingAddress.toInt()+i]
            if (displayMemory.collision) {
                collisionValue = 1u
            }
        }

        memoryManager.registers[0xF] = collisionValue.toUByte()

        LOG.debug { "DRW V${toHex(x)}, V${toHex(y)}, ${toHex(spriteHeight.toUByte())}" }
    }

    /**
     * Ex9E - SKP Vx
     * Skip next instruction if key with the value of Vx is pressed.
     * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position,
     * PC is increased by 2.
     */
    fun skipIfKeyPressed(value: UInt) {
        val x = x(value)
        val registerValue = memoryManager.registers[x.toInt()]

        if (inputManager.isActive(registerValue.toInt())) {
            memoryManager.skipNextInstruction()
        }
        LOG.debug { "SKP V${toHex(x)}" }
    }

    /**
     * ExA1 - SKNP Vx
     * Skip next instruction if key with the value of Vx is not pressed.
     * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position,
     * PC is increased by 2.
     */
    fun skipIfKeyNotPressed(value: UInt) {
        val x = x(value)
        val registerValue = memoryManager.registers[x.toInt()]

        if (!inputManager.isActive(registerValue.toInt())) {
            memoryManager.skipNextInstruction()
        }
        LOG.debug { "SKNP V${toHex(x)}" }
    }

    /**
     * Fx07 - LD Vx, DT
     * Set Vx = delay timer value.
     * The value of DT is placed into Vx.
     */
    fun setRegisterToDelayTimerValue(value: UInt) {
        val x = x(value)
        val delayTimerValue = delayRegister.value

        memoryManager.registers[x.toInt()] = delayTimerValue

        LOG.debug { "LD V${toHex(x)}, DT ($delayTimerValue)" }
    }

    /**
     * Fx0A - LD Vx, K
     * Wait for a key press, store the value of the key in Vx.
     * All execution stops until a key is pressed, then the value of that key is stored in Vx.
     *
     * Pause the CPU until a key is pressed. The Chip8InputManager deals with un-pausing the CPU once a key is pressed.
     */
    fun waitForKeyPress(value: UInt) {
        cpuState = PAUSED

        LOG.debug { "Pausing CPU" }
        LOG.debug { "LD V${x(value)}, K" }
    }

    /**
     * Fx15 - LD DT, Vx
     * Set delay timer = Vx.
     * DT is set equal to the value of Vx.
     */
    fun setDelayTimerRegisterToValueInGeneralRegister(value: UInt) {
        val x = x(value)

        delayRegister.value = memoryManager.registers[x.toInt()]

        LOG.debug { "LD DT, V${toHex(x)}" }
    }

    /**
     * Fx18 - LD ST, Vx
     * Set sound timer = Vx.
     * ST is set equal to the value of Vx.
     */
    fun setSoundTimerRegisterToValueInGeneralRegister(value: UInt) {
        val x = x(value)

        soundRegister.value = memoryManager.registers[x.toInt()]

        LOG.debug { "LD ST, V${toHex(x)}" }
    }

    /**
     * Fx1E - ADD I, Vx
     * Set I = I + Vx.
     * The values of I and Vx are added, and the results are stored in I.
     */
    fun addGeneralRegisterToIRegister(value: UInt) {
        val x = x(value)

        // I is a 16 bit register
        memoryManager.i = (memoryManager.i + memoryManager.registers[x.toInt()]) % 0xFFFFu

        LOG.debug { "ADD I, V${toHex(x)}" }
    }

    /**
     * Fx29 - LD F, Vx
     * Set I = location of sprite for digit Vx.
     * The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx.
     */
    fun loadIRegisterWithLocationOfSpriteForDigit(value: UInt) {
        val x = x(value)

        memoryManager.i = memoryManager.getLocationOfSpriteDigit(memoryManager.registers[x.toInt()].toUInt())

        LOG.debug { "LD F, V${toHex(x)}" }
    }

    /**
     * Fx33 - LD B, Vx
     * Store BCD representation of Vx in memory locations I, I+1, and I+2.
     * The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I,
     * the tens digit at location I+1, and the ones digit at location I+2.
     */
    fun storeBCDRepresentation(value: UInt) {
        val x = x(value)
        val xValueString = memoryManager.registers[x.toInt()].toString()

        var nextLocation = memoryManager.i

        for (i in 0 until 3-xValueString.length) {
            memoryManager.ram[nextLocation.toInt()] = 0u
            nextLocation++
        }

        for (i in xValueString.indices) {
            memoryManager.ram[nextLocation.toInt()] = xValueString[i].toString().toUByte()
            nextLocation++
        }

        LOG.debug { "LD B, V${toHex(x)}" }
    }

    /**
     * Fx55 - LD \[I], Vx
     * Store registers V0 through Vx (inclusive) in memory starting at location I.
     * The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
     */
    fun loadAllGeneralRegistersIntoMemory(value: UInt) {
        val x = x(value)
        val i = memoryManager.i

        for (j in 0..x.toInt()) {
            // TODO: Should this wrapping behaviour actually be in the ValidatedMemory class?
            val memoryLocation = (i.toInt() + j) % memorySize

            memoryManager.ram[memoryLocation] = memoryManager.registers[j]
        }

        // https://github.com/mattmikolay/chip-8/wiki/Mastering-CHIP%E2%80%908#chip-8-instructions states that I is
        // set to i + x + 1 after the operation but the test roms suggest that's not the case.
        // Also, The Wikipedia says this:
        // "In the original CHIP-8 implementation, and also in CHIP-48, I is left incremented after this instruction
        // had been executed. In SCHIP, I is left unmodified."
        if (FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX55)) {
            // TODO: Should this wrapping behaviour actually be in the ValidatedMemory class?
            memoryManager.i += ((x + 1u) % memorySize.toUInt())
        }

        LOG.debug { "LD [I], V${toHex(x)}" }
    }

    /**
     * Fx65 - LD Vx, \[I]
     * Read registers V0 through Vx from memory starting at location I.
     * The interpreter reads values from memory starting at location I into registers V0 through Vx.
     */
    fun readMemoryIntoAllGeneralRegisters(value: UInt) {
        val x = x(value)
        val i = memoryManager.i

        for (j in 0..x.toInt()) {
            // TODO: Should this wrapping behaviour actually be in the ValidatedMemory class?
            val memoryLocation = (i.toInt() + j) % memorySize

            memoryManager.registers[j] = memoryManager.ram[memoryLocation]
        }

        // https://github.com/mattmikolay/chip-8/wiki/Mastering-CHIP%E2%80%908#chip-8-instructions states that I is
        // set to i + x + 1 after the operation.
        // The Wikipedia says this:
        // "In the original CHIP-8 implementation, and also in CHIP-48, I is left incremented after this
        // instruction had been executed. In SCHIP, I is left unmodified."

        if (FeatureManager.isEnabled(SystemDependentInstructionFeature.I_INCREMENT_FX65)) {
            // TODO: Should this wrapping behaviour actually be in the ValidatedMemory class?
            memoryManager.i += ((x + 1u) % memorySize.toUInt())
        }

        LOG.debug { "LD V${toHex(x)}, [I]" }
    }

    companion object {
        private val LOG = logger(this::class)
    }
}