package cpu

import leftNibble
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rightByte
import rightNibble
import wordHex

class InstructionDecoder {
    private lateinit var cpu: Cpu

    fun setCpu(cpu: Cpu) {
        this.cpu = cpu
    }

    fun decode(instruction: UInt): (UInt) -> Unit {
        val unrecognisedOpcodeException = IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")

        LOG.trace("Decoding instruction: ${wordHex(instruction)}")

        return when (instruction.toInt()) {
            0x00E0 -> cpu::clearScreen
            0x00EE -> cpu::doReturn
            else -> {
                return when (leftNibble(instruction).toInt()) {
                    0x0 -> cpu::sysCall
                    0x1 -> cpu::jump
                    0x2 -> cpu::call
                    0x3 -> cpu::skipIfRegisterAndMemoryEqual
                    0x4 -> cpu::skipIfRegisterAndMemoryNotEqual
                    0x5 -> {
                        if (rightNibble(instruction).toInt() == 0) {
                            return cpu::skipIfRegisterAndRegisterEqual
                        } else {
                            throw unrecognisedOpcodeException
                        }
                    }
                    0x6 -> cpu::loadMemoryIntoRegister
                    0x7 -> cpu::addMemoryToRegister
                    0x8 -> {
                        return when (rightNibble(instruction).toInt()) {
                            0x0 -> cpu::loadRegisterIntoRegister
                            0x1 -> cpu::or
                            0x2 -> cpu::and
                            0x3 -> cpu::xor
                            0x4 -> cpu::addRegisterAndRegister
                            0x5 -> cpu::subtractYRegisterFromXRegister
                            0x6 -> cpu::shiftRight
                            0x7 -> cpu::subtractXRegisterFromYRegister
                            0xE -> cpu::shiftLeft
                            else -> throw unrecognisedOpcodeException
                        }
                    }
                    0x9 -> {
                        if (rightNibble(instruction).toInt() == 0) {
                            return cpu::skipIfRegisterAndRegisterNotEqual
                        } else {
                            throw unrecognisedOpcodeException
                        }
                    }
                    0xA -> cpu::loadMemoryIntoIRegister
                    0xB -> cpu::jumpWithOffset
                    0xC -> cpu::random
                    0xD -> cpu::draw
                    0xE -> {
                        return when(rightByte(instruction).toInt()) {
                            0x9E -> cpu::skipIfKeyPressed
                            0xA1 -> cpu::skipIfKeyNotPressed
                            else -> throw unrecognisedOpcodeException
                        }
                    }
                    0xF -> {
                        return when(rightByte(instruction).toInt()) {
                            0x07 -> cpu::setRegisterToDelayTimerValue
                            0x0A -> cpu::waitForKeyPress
                            0x15 -> cpu::setDelayTimerRegisterToValueInGeneralRegister
                            0x18 -> cpu::setSoundTimerRegisterToValueInGeneralRegister
                            0x1E -> cpu::addIRegisterToGeneralRegister
                            0x29 -> cpu::loadIRegisterWithLocationOfSpriteForDigit
                            0x33 -> cpu::storeBCDRepresentation
                            0x55 -> cpu::loadAllGeneralRegistersIntoMemory
                            0x65 -> cpu::readMemoryIntoAllGeneralRegisters
                            else -> throw unrecognisedOpcodeException
                        }
                    }
                    else -> throw unrecognisedOpcodeException
                }
            }
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(InstructionDecoder::class.java)
    }
}