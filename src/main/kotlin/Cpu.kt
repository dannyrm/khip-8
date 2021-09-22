import memory.MemoryManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.lang.IllegalArgumentException

class Cpu(private val memoryManager: MemoryManager) {

    fun decode(instruction: UInt): (UInt) -> Unit {
        return when (instruction.toInt()) {
            0x00E0 -> ::clearScreen
            0x00EE -> ::doReturn
            else -> {
                return when (leftNibble(instruction).toInt()) {
                    0x0 -> ::sysCall
                    0x1 -> ::jump
                    0x2 -> ::call
                    0x3 -> ::skipIfRegisterAndMemoryEqual
                    0x4 -> ::skipIfNotEqual
                    0x5 -> ::skipIfRegisterAndRegisterEqual
                    0x6 -> ::loadMemoryIntoRegister
                    0x7 -> ::addMemoryToRegister
                    0x8 -> {
                        return when (rightNibble(instruction).toInt()) {
                            0x0 -> ::loadRegisterIntoRegister
                            0x1 -> ::or
                            0x2 -> ::and
                            0x3 -> ::xor
                            0x4 -> ::addRegisterAndRegister
                            0x5 -> ::subtractRegisterAndRegister
                            0x6 -> ::shiftRight
                            0x7 -> ::subtractRegisterAndRegisterNegate
                            0xE -> ::shiftLeft
                            else -> throw IllegalArgumentException("Unrecognised opcode: $instruction")
                        }
                    }
                    0x9 -> ::skipIfRegisterAndRegisterNotEqual
                    0xA -> ::loadMemoryIntoIRegister
                    0xB -> ::jumpWithOffset
                    0xC -> ::random
                    0xD -> ::draw
                    0xE -> {
                        return when(rightNibble(instruction).toInt()) {
                            0xE -> ::skipIfKeyPressed
                            0x1 -> ::skipIfKeyNotPressed
                            else -> throw IllegalArgumentException("Unrecognised opcode: $instruction")
                        }
                    }
                    0xF -> {
                        return when(rightByte(instruction).toInt()) {
                            0x07 -> ::setRegisterToDelayTimerValue
                            0x0A -> ::waitForKeyPress
                            0x15 -> ::setDelayTimerRegisterToValueInGeneralRegister
                            0x18 -> ::setSoundTimerRegisterToValueInGeneralRegister
                            0x1E -> ::addIRegisterToGeneralRegister
                            0x29 -> ::loadIRegisterWithLocationOfSpriteForDigit
                            0x33 -> ::storeBCDRepresentation
                            0x55 -> ::loadAllGeneralRegistersIntoMemory
                            0x65 -> ::readMemoryIntoAllGeneralRegisters
                            else -> throw IllegalArgumentException("Unrecognised opcode: $instruction")
                        }
                    }
                    else -> throw IllegalArgumentException("Unrecognised opcode: $instruction")
                }
            }
        }
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
    fun skipIfNotEqual(value: UInt) {
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
    fun subtractRegisterAndRegister(value: UInt) {
    }

    // TODO Is it negate?
    fun subtractRegisterAndRegisterNegate(value: UInt) {
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

// Lowest 8 bits of the instruction (kk or byte)
fun rightByte(value: UInt): UByte = (value and 0xFF.toUInt()).toUByte()

// Highest 4 bits of instruction (n or nibble)
fun leftNibble(value: UInt): UByte = (value and 0xF000.toUInt() shr 12).toUByte()

// Lowest 4 bits of instruction (n or nibble)
fun rightNibble(value: UInt): UByte = (value and 0xF.toUInt()).toUByte()

// Lower 4 bits of the high byte of instruction
fun x(value: UInt) : UByte = (value and 0xF00.toUInt() shr 8).toUByte()

// Upper 4 bits of the low byte of instruction
fun y(value: UInt) : UByte = (value and 0xF0.toUInt() shr 4).toUByte()

// Lowest 12 bits of instruction (nnn or addr)
fun rightNibbleByte(value: UInt): UInt = value and 0xFFF.toUInt()