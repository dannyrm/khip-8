package uk.co.dmatthews.khip8.cpu

import kotlinx.coroutines.delay
import uk.co.dmatthews.khip8.memory.MemoryManager
import nibbleByteHex
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rightByte
import rightNibbleByte
import toHex
import uk.co.dmatthews.khip8.memory.DisplayMemory
import x

class Cpu(private val memoryManager: MemoryManager,
          private val instructionDecoder: InstructionDecoder,
          private var halt: Boolean = false) {

    suspend fun start() {
        LOG.debug("Starting CPU...")

        while (!halt) {
            // FETCH .. DECODE .. EXECUTE
            val instruction = memoryManager.fetchNextInstruction()
            val decodedInstruction = instructionDecoder.decode(instruction)
            decodedInstruction.invoke(instruction)
            delay(FREQUENCY_IN_MILLIS)
        }
    }

    fun halt() {
        LOG.debug("Halting CPU...")
        halt = true
    }

    /**
     * 0nnn - SYS addr
     * Jump to a machine code routine at nnn.
     * This instruction is only used on the old computers on which Chip-8 was originally implemented. It is ignored by modern interpreters.
     * TODO
     */
    fun sysCall(value: UInt) {
        LOG.debug("SYS addr")
    }

    /**
     * 00E0 - CLS
     * Clear the display.
     */
    fun clearScreen(unusedValue: UInt) {
        memoryManager.displayMemory.clear()
        LOG.debug("CLS")
    }

    /**
     * 00EE - RET
     * Sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
     */
    fun doReturn(unusedValue: UInt) {
        memoryManager.PC = memoryManager.stack.pop()

        LOG.debug("RET")
    }

    /**
     * 1nnn - JP
     * Jump to location nnn - The interpreter sets the program counter to nnn.
     */
    fun jump(value: UInt) {
        val nnn = rightNibbleByte(value)

        memoryManager.PC = nnn

        LOG.debug("JP ${nibbleByteHex(nnn)}")
    }

    /**
     * 2nnn - CALL
     * Call subroutine at nnn.
     * Increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
     */
    fun call(value: UInt) {
        val nnn = rightNibbleByte(value)

        memoryManager.stack.push(memoryManager.PC)
        memoryManager.PC = nnn

        LOG.debug("CALL ${nibbleByteHex(nnn)}")
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
            memoryManager.PC++
        }

        LOG.debug("SE V${toHex(x)}, ${toHex(byte)}")
    }

    /**
     * 4xkk - SNE Vx, byte
     * Skip next instruction if Vx != kk.
     * The interpreter compares register Vx to kk, and if they are not equal, increments the program counter by 2.
     * TODO
     */
    fun skipIfRegisterAndMemoryNotEqual(value: UInt) {
        LOG.debug("SNE Vx, byte")
    }

    /**
     * 5xy0 - SE Vx, Vy
     * Skip next instruction if Vx = Vy.
     * The interpreter compares register Vx to register Vy, and if they are equal, increments the program counter by 2.
     * TODO
     */
    fun skipIfRegisterAndRegisterEqual(value: UInt) {
        LOG.debug("SE Vx, Vy")
    }

    /**
     * 6xkk - LD Vx, byte
     * Set Vx = kk.
     * The interpreter puts the value kk into register Vx.
     * TODO
     */
    fun loadMemoryIntoRegister(value: UInt) {
        LOG.debug("LD Vx, byte")
    }

    /**
     * 7xkk - ADD Vx, byte
     * Set Vx = Vx + kk.
     * Adds the value kk to the value of register Vx, then stores the result in Vx.
     * TODO
     */
    fun addMemoryToRegister(value: UInt) {
        LOG.debug("ADD Vx, byte")
    }

    /**
     * 8xy0 - LD Vx, Vy
     * Set Vx = Vy.
     * Stores the value of register Vy in register Vx.
     * TODO
     */
    fun loadRegisterIntoRegister(value: UInt) {
        LOG.debug("LD Vx, Vy")
    }

    /**
     * 8xy1 - OR Vx, Vy
     * Set Vx = Vx OR Vy.
     * Performs a bitwise OR on the values of Vx and Vy, then stores the result in Vx.
     * TODO
     */
    fun or(value: UInt) {
        LOG.debug("OR Vx, Vy")
    }

    /**
     * 8xy2 - AND Vx, Vy
     * Set Vx = Vx AND Vy.
     * Performs a bitwise AND on the values of Vx and Vy, then stores the result in Vx. A bitwise AND compares the corrseponding bits from two values, and if both bits are 1, then the same bit in the result is also 1. Otherwise, it is 0.
     * TODO
     */
    fun and(value: UInt) {
        LOG.debug("AND Vx, Vy")
    }

    /**
     * 8xy3 - XOR Vx, Vy
     * Set Vx = Vx XOR Vy.
     * Performs a bitwise exclusive OR on the values of Vx and Vy, then stores the result in Vx.
     * TODO
     */
    fun xor(value: UInt) {
        LOG.debug("XOR Vx, Vy")
    }

    /**
     * 8xy4 - ADD Vx, Vy
     * Set Vx = Vx + Vy, set VF = carry.
     * The values of Vx and Vy are added together.
     * If the result is greater than 8 bits (i.e., > 255,) VF is set to 1, otherwise 0.
     * Only the lowest 8 bits of the result are kept, and stored in Vx.
     * TODO
     */
    fun addRegisterAndRegister(value: UInt) {
        LOG.debug("ADD Vx, Vy")
    }

    /**
     * 8xy5 - SUB Vx, Vy
     * Set Vx = Vx - Vy, set VF = NOT borrow.
     * If Vx > Vy, then VF is set to 1, otherwise 0. Then Vy is subtracted from Vx, and the results stored in Vx.
     * TODO
     */
    fun subtractYRegisterFromXRegister(value: UInt) {
        LOG.debug("SUB Vx, Vy")
    }

    /**
     * 8xy6 - SHR Vx {, Vy}
     * Set Vx = Vx SHR 1.
     * If the least-significant bit of Vx is 1, then VF is set to 1, otherwise 0. Then Vx is divided by 2.
     * TODO
     */
    fun shiftRight(value: UInt) {
        LOG.debug("SHR Vx {, Vy}")
    }

    /**
     * 8xy7 - SUBN Vx, Vy
     * Set Vx = Vy - Vx, set VF = NOT borrow.
     * If Vy > Vx, then VF is set to 1, otherwise 0. Then Vx is subtracted from Vy, and the results stored in Vx.
     * TODO
     */
    fun subtractXRegisterFromYRegister(value: UInt) {
        LOG.debug("SUBN Vx, Vy")
    }

    /**
     * 8xyE - SHL Vx {, Vy}
     * Set Vx = Vx SHL 1.
     * If the most-significant bit of Vx is 1, then VF is set to 1, otherwise to 0. Then Vx is multiplied by 2.
     * TODO
     */
    fun shiftLeft(value: UInt) {
        LOG.debug("SHL Vx {, Vy}")
    }

    /**
     * 9xy0 - SNE Vx, Vy
     * Skip next instruction if Vx != Vy.
     * The values of Vx and Vy are compared, and if they are not equal, the program counter is increased by 2.
     * TODO
     */
    fun skipIfRegisterAndRegisterNotEqual(value: UInt) {
        LOG.debug("SNE Vx, Vy")
    }

    /**
     * Annn - LD I, addr
     * Set I = nnn.
     * The value of register I is set to nnn.
     * TODO
     */
    fun loadMemoryIntoIRegister(value: UInt) {
        LOG.debug("LD I, addr")
    }

    /**
     * Bnnn - JP V0, addr
     * Jump to location nnn + V0.
     * The program counter is set to nnn plus the value of V0.
     * TODO
     */
    fun jumpWithOffset(value: UInt) {
        LOG.debug("JP V0, addr")
    }

    /**
     * Cxkk - RND Vx, byte
     * Set Vx = random byte AND kk.
     * The interpreter generates a random number from 0 to 255, which is then ANDed with the value kk.
     * The results are stored in Vx.
     * TODO
     */
    fun random(value: UInt) {
        LOG.debug("RND Vx, byte")
    }

    /**
     *
     * Dxyn - DRW Vx, Vy, nibble
     * Display n-byte sprite starting at memory location I at (Vx, Vy), set VF = collision.
     * The interpreter reads n bytes from memory, starting at the address stored in I.
     * These bytes are then displayed as sprites on screen at coordinates (Vx, Vy).
     * Sprites are XORed onto the existing screen.
     * If this causes any pixels to be erased, VF is set to 1, otherwise it is set to 0.
     * If the sprite is positioned so part of it is outside the coordinates of the display,
     * it wraps around to the opposite side of the screen.
     * TODO
     */
    fun draw(value: UInt) {
        LOG.debug("DRW Vx, Vy, nibble")
    }

    /**
     * Ex9E - SKP Vx
     * Skip next instruction if key with the value of Vx is pressed.
     * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the down position,
     * PC is increased by 2.
     * TODO
     */
    fun skipIfKeyPressed(value: UInt) {
    }

    /**
     * ExA1 - SKNP Vx
     * Skip next instruction if key with the value of Vx is not pressed.
     * Checks the keyboard, and if the key corresponding to the value of Vx is currently in the up position,
     * PC is increased by 2.
     * TODO
     */
    fun skipIfKeyNotPressed(value: UInt) {
        LOG.debug("SKNP Vx")
    }

    /**
     * Fx07 - LD Vx, DT
     * Set Vx = delay timer value.
     * The value of DT is placed into Vx.
     * TODO
     */
    fun setRegisterToDelayTimerValue(value: UInt) {
        LOG.debug("LD Vx, DT")
    }

    /**
     * Fx0A - LD Vx, K
     * Wait for a key press, store the value of the key in Vx.
     * All execution stops until a key is pressed, then the value of that key is stored in Vx.
     * TODO
     */
    fun waitForKeyPress(value: UInt) {
        LOG.debug("LD Vx, K")
    }

    /**
     * Fx15 - LD DT, Vx
     * Set delay timer = Vx.
     * DT is set equal to the value of Vx.
     * TODO
     */
    fun setDelayTimerRegisterToValueInGeneralRegister(value: UInt) {
        LOG.debug("LD DT, Vx")
    }

    /**
     * Fx18 - LD ST, Vx
     * Set sound timer = Vx.
     * ST is set equal to the value of Vx.
     * TODO
     */
    fun setSoundTimerRegisterToValueInGeneralRegister(value: UInt) {
        LOG.debug("LD ST, Vx")
    }

    /**
     * Fx1E - ADD I, Vx
     * Set I = I + Vx.
     * The values of I and Vx are added, and the results are stored in I.
     * TODO
     */
    fun addIRegisterToGeneralRegister(value: UInt) {
        LOG.debug("ADD I, Vx")
    }

    /**
     * Fx29 - LD F, Vx
     * Set I = location of sprite for digit Vx.
     * The value of I is set to the location for the hexadecimal sprite corresponding to the value of Vx.
     * TODO
     */
    fun loadIRegisterWithLocationOfSpriteForDigit(value: UInt) {
        LOG.debug("LD F, Vx")
    }

    /**
     * Fx33 - LD B, Vx
     * Store BCD representation of Vx in memory locations I, I+1, and I+2.
     * The interpreter takes the decimal value of Vx, and places the hundreds digit in memory at location in I,
     * the tens digit at location I+1, and the ones digit at location I+2.
     * TODO
     */
    fun storeBCDRepresentation(value: UInt) {
        LOG.debug("LD B, Vx")
    }

    /**
     * Fx55 - LD [I], Vx
     * Store registers V0 through Vx in memory starting at location I.
     * The interpreter copies the values of registers V0 through Vx into memory, starting at the address in I.
     * TODO
     */
    fun loadAllGeneralRegistersIntoMemory(value: UInt) {
        LOG.debug("LD [I], Vx")
    }

    /**
     * Fx65 - LD Vx, [I]
     * Read registers V0 through Vx from memory starting at location I.
     * The interpreter reads values from memory starting at location I into registers V0 through Vx.
     * TODO
     */
    fun readMemoryIntoAllGeneralRegisters(value: UInt) {
        LOG.debug("LD Vx, [I]")
    }

    companion object {
        // 500 Hz, calculated as 1000 / 500 = 2.
        const val FREQUENCY_IN_MILLIS = 17L
        private val LOG: Logger = LoggerFactory.getLogger(Cpu::class.java)
    }
}
