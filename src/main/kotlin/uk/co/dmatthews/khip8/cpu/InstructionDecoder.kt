package uk.co.dmatthews.khip8.cpu

import leftNibble
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import rightByte
import rightNibble
import uk.co.dmatthews.khip8.executors.InstructionExecutor
import wordHex
import kotlin.reflect.KFunction2

class InstructionDecoder {
    fun decode(instruction: UInt, instructionExecutor: InstructionExecutor) {
        LOG.trace("Decoding instruction: ${wordHex(instruction)}")

        when (instruction.toInt()) {
            0x00E0 -> instructionExecutor.clearScreen(instruction)
            0x00EE -> instructionExecutor.doReturn(instruction)
            else -> {
                when (leftNibble(instruction).toInt()) {
                    0x0 -> instructionExecutor.sysCall(instruction)
                    0x1 -> instructionExecutor.jump(instruction)
                    0x2 -> instructionExecutor.call(instruction)
                    0x3 -> instructionExecutor.skipIfRegisterAndMemoryEqual(instruction)
                    0x4 -> instructionExecutor.skipIfRegisterAndMemoryNotEqual(instruction)
                    0x5 -> decode0x5Instruction(instruction, instructionExecutor)
                    0x6 -> instructionExecutor.loadMemoryIntoRegister(instruction)
                    0x7 -> instructionExecutor.addValueToRegister(instruction)
                    0x8 -> decode0x8Instruction(instruction, instructionExecutor)
                    0x9 -> decode0x9Instruction(instruction, instructionExecutor)
                    0xA -> instructionExecutor.loadMemoryIntoIRegister(instruction)
                    0xB -> instructionExecutor.jumpWithOffset(instruction)
                    0xC -> instructionExecutor.random(instruction)
                    0xD -> instructionExecutor.draw(instruction)
                    0xE -> decode0xEInstruction(instruction, instructionExecutor)
                    0xF -> decode0xFInstruction(instruction, instructionExecutor)
                    else -> throw IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")
                }
            }
        }
    }

    private fun decode0x5Instruction(instruction: UInt, instructionExecutor: InstructionExecutor) {
        if (rightNibble(instruction).toInt() == 0) {
            instructionExecutor.skipIfRegisterAndRegisterEqual(instruction)
        } else {
            throw throw IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")
        }
    }

    private fun decode0x8Instruction(instruction: UInt, instructionExecutor: InstructionExecutor) {
        when (rightNibble(instruction).toInt()) {
            0x0 -> instructionExecutor.loadRegisterIntoRegister(instruction)
            0x1 -> instructionExecutor.or(instruction)
            0x2 -> instructionExecutor.and(instruction)
            0x3 -> instructionExecutor.xor(instruction)
            0x4 -> instructionExecutor.addRegisterAndRegister(instruction)
            0x5 -> instructionExecutor.subtractYRegisterFromXRegister(instruction)
            0x6 -> instructionExecutor.shiftRightXOnlyVariant(instruction)
            0x7 -> instructionExecutor.subtractXRegisterFromYRegister(instruction)
            0xE -> instructionExecutor.shiftLeftXOnlyVariant(instruction)
            else -> throw IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")
        }
    }

    private fun decode0x9Instruction(instruction: UInt, instructionExecutor: InstructionExecutor) {
        if (rightNibble(instruction).toInt() == 0) {
            instructionExecutor.skipIfRegisterAndRegisterNotEqual(instruction)
        } else {
            throw IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")
        }
    }

    private fun decode0xEInstruction(instruction: UInt, instructionExecutor: InstructionExecutor) {
        when(rightByte(instruction).toInt()) {
            0x9E -> instructionExecutor.skipIfKeyPressed(instruction)
            0xA1 -> instructionExecutor.skipIfKeyNotPressed(instruction)
            else -> throw IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")
        }
    }

    private fun decode0xFInstruction(instruction: UInt, instructionExecutor: InstructionExecutor) {
        when(rightByte(instruction).toInt()) {
            0x07 -> instructionExecutor.setRegisterToDelayTimerValue(instruction)
            0x0A -> instructionExecutor.waitForKeyPress(instruction)
            0x15 -> instructionExecutor.setDelayTimerRegisterToValueInGeneralRegister(instruction)
            0x18 -> instructionExecutor.setSoundTimerRegisterToValueInGeneralRegister(instruction)
            0x1E -> instructionExecutor.addGeneralRegisterToIRegister(instruction)
            0x29 -> instructionExecutor.loadIRegisterWithLocationOfSpriteForDigit(instruction)
            0x33 -> instructionExecutor.storeBCDRepresentation(instruction)
            0x55 -> instructionExecutor.loadAllGeneralRegistersIntoMemory(instruction)
            0x65 -> instructionExecutor.readMemoryIntoAllGeneralRegisters(instruction)
            else -> throw IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(InstructionDecoder::class.java)
    }
}