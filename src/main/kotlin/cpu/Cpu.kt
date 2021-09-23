package cpu

import memory.MemoryManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rightByte
import rightNibbleByte
import x
import java.io.File

class Cpu(private val memoryManager: MemoryManager,
          private val instructionDecoder: InstructionDecoder,
          var halt: Boolean = false) {

    fun start() {
        LOG.trace("Starting CPU...")

        while (!halt) {
            // FETCH .. DECODE .. EXECUTE
            val instruction = memoryManager.fetchNextInstruction()
            val decodedInstruction = instructionDecoder.decode(instruction)
            decodedInstruction.invoke(instruction)
        }
    }

    fun halt() {
        LOG.trace("Halting CPU...")
        halt = true
    }

    /**
     * 00E0 - CLS
     * Clear the display.
     * TODO: implement screen clearing
     */
    fun clearScreen(value: UInt) {
        LOG.trace("CLS")
    }

    /**
     * 00EE - RET
     * Sets the program counter to the address at the top of the stack, then subtracts 1 from the stack pointer.
     */
    fun doReturn(value: UInt) {
        memoryManager.PC = memoryManager.stack.pop()

        LOG.trace("RET ${memoryManager.PC}")
    }

    /**
     * 1nnn - JP
     * Jump to location nnn - The interpreter sets the program counter to nnn.
     */
    fun jump(value: UInt) {
        memoryManager.PC = rightNibbleByte(value)

        LOG.trace("JP ${memoryManager.PC}")
    }

    /**
     * 2nnn - CALL
     * Call subroutine at nnn.
     * Increments the stack pointer, then puts the current PC on the top of the stack. The PC is then set to nnn.
     */
    fun call(value: UInt) {
        memoryManager.stack.push(memoryManager.PC)
        memoryManager.PC = rightNibbleByte(value)

        LOG.trace("CALL ${memoryManager.PC}")
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

        LOG.trace("SE V$x, $byte")
    }

    // TODO
    fun skipIfRegisterAndMemoryNotEqual(value: UInt) {
    }

    // TODO
    fun skipIfRegisterAndRegisterEqual(value: UInt) {
    }

    // TODO
    fun skipIfRegisterAndRegisterNotEqual(value: UInt) {
    }

    // TODO
    fun loadMemoryIntoRegister(value: UInt) {
    }

    // TODO
    fun addMemoryToRegister(value: UInt) {
    }

    // TODO
    fun loadMemoryIntoIRegister(value: UInt) {
    }

    // TODO
    fun jumpWithOffset(value: UInt) {
    }

    // TODO
    fun random(value: UInt) {
    }

    // TODO
    fun draw(value: UInt) {
    }

    // TODO
    fun sysCall(value: UInt) {
    }

    // TODO
    fun loadRegisterIntoRegister(value: UInt) {
    }

    // TODO
    fun or(value: UInt) {
    }

    // TODO
    fun and(value: UInt) {
    }

    // TODO
    fun xor(value: UInt) {
    }

    // TODO
    fun addRegisterAndRegister(value: UInt) {
    }

    // TODO
    fun subtractYRegisterFromXRegister(value: UInt) {
    }

    // TODO Is it negate?
    fun subtractXRegisterFromYRegister(value: UInt) {
    }

    // TODO
    fun shiftRight(value: UInt) {
    }

    // TODO
    fun shiftLeft(value: UInt) {
    }

    // TODO
    fun skipIfKeyPressed(value: UInt) {
    }

    // TODO
    fun skipIfKeyNotPressed(value: UInt) {
    }

    // TODO
    fun setRegisterToDelayTimerValue(value: UInt) {
    }

    // TODO
    fun waitForKeyPress(value: UInt) {
    }

    // TODO
    fun setDelayTimerRegisterToValueInGeneralRegister(value: UInt) {
    }

    // TODO
    fun setSoundTimerRegisterToValueInGeneralRegister(value: UInt) {
    }

    // TODO
    fun addIRegisterToGeneralRegister(value: UInt) {
    }

    // TODO
    fun loadIRegisterWithLocationOfSpriteForDigit(value: UInt) {
    }

    // TODO
    fun storeBCDRepresentation(value: UInt) {
    }

    // TODO
    fun loadAllGeneralRegistersIntoMemory(value: UInt) {
    }

    // TODO
    fun readMemoryIntoAllGeneralRegisters(value: UInt) {
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(Cpu::class.java)
    }
}
