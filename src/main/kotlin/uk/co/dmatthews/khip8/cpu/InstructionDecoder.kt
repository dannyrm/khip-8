package uk.co.dmatthews.khip8.cpu

import leftNibble
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rightByte
import rightNibble
import wordHex
import kotlin.reflect.KFunction2

class InstructionDecoder {
    fun decode(instruction: UInt): KFunction2<Cpu, UInt, Unit> {
        val unrecognisedOpcodeException = IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")

        LOG.trace("Decoding instruction: ${wordHex(instruction)}")

        return when (instruction.toInt()) {
            0x00E0 -> Cpu::clearScreen
            0x00EE -> Cpu::doReturn
            else -> {
                return when (leftNibble(instruction).toInt()) {
                    0x0 -> Cpu::sysCall
                    0x1 -> Cpu::jump
                    0x2 -> Cpu::call
                    0x3 -> Cpu::skipIfRegisterAndMemoryEqual
                    0x4 -> Cpu::skipIfRegisterAndMemoryNotEqual
                    0x5 -> {
                        if (rightNibble(instruction).toInt() == 0) {
                            return Cpu::skipIfRegisterAndRegisterEqual
                        } else {
                            throw unrecognisedOpcodeException
                        }
                    }
                    0x6 -> Cpu::loadMemoryIntoRegister
                    0x7 -> Cpu::addValueToRegister
                    0x8 -> {
                        return when (rightNibble(instruction).toInt()) {
                            0x0 -> Cpu::loadRegisterIntoRegister
                            0x1 -> Cpu::or
                            0x2 -> Cpu::and
                            0x3 -> Cpu::xor
                            0x4 -> Cpu::addRegisterAndRegister
                            0x5 -> Cpu::subtractYRegisterFromXRegister
                            0x6 -> Cpu::shiftRight
                            0x7 -> Cpu::subtractXRegisterFromYRegister
                            0xE -> Cpu::shiftLeft
                            else -> throw unrecognisedOpcodeException
                        }
                    }
                    0x9 -> {
                        if (rightNibble(instruction).toInt() == 0) {
                            return Cpu::skipIfRegisterAndRegisterNotEqual
                        } else {
                            throw unrecognisedOpcodeException
                        }
                    }
                    0xA -> Cpu::loadMemoryIntoIRegister
                    0xB -> Cpu::jumpWithOffset
                    0xC -> Cpu::random
                    0xD -> Cpu::draw
                    0xE -> {
                        return when(rightByte(instruction).toInt()) {
                            0x9E -> Cpu::skipIfKeyPressed
                            0xA1 -> Cpu::skipIfKeyNotPressed
                            else -> throw unrecognisedOpcodeException
                        }
                    }
                    0xF -> {
                        return when(rightByte(instruction).toInt()) {
                            0x07 -> Cpu::setRegisterToDelayTimerValue
                            0x0A -> Cpu::waitForKeyPress
                            0x15 -> Cpu::setDelayTimerRegisterToValueInGeneralRegister
                            0x18 -> Cpu::setSoundTimerRegisterToValueInGeneralRegister
                            0x1E -> Cpu::addGeneralRegisterToIRegister
                            0x29 -> Cpu::loadIRegisterWithLocationOfSpriteForDigit
                            0x33 -> Cpu::storeBCDRepresentation
                            0x55 -> Cpu::loadAllGeneralRegistersIntoMemory
                            0x65 -> Cpu::readMemoryIntoAllGeneralRegisters
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