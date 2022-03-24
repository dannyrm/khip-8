package com.github.dannyrm.khip8.cpu

import leftNibble
import rightByte
import rightNibble
import com.github.dannyrm.khip8.executors.InstructionExecutor
import com.github.dannyrm.khip8.logger
import wordHex

class InstructionDecoder {
    fun decodeAndExecute(instruction: UInt, instructionExecutors: List<InstructionExecutor>) {
        LOG.trace { "Decoding instruction: ${wordHex(instruction)}" }

        when (instruction.toInt()) {
            0x00E0 -> instructionExecutors.forEach { it.clearScreen(instruction) }
            0x00EE -> instructionExecutors.forEach { it.doReturn(instruction) }
            else -> {
                when (leftNibble(instruction).toInt()) {
                    0x0 -> instructionExecutors.forEach { it.sysCall(instruction) }
                    0x1 -> instructionExecutors.forEach { it.jump(instruction) }
                    0x2 -> instructionExecutors.forEach { it.call(instruction) }
                    0x3 -> instructionExecutors.forEach { it.skipIfRegisterAndMemoryEqual(instruction) }
                    0x4 -> instructionExecutors.forEach { it.skipIfRegisterAndMemoryNotEqual(instruction) }
                    0x5 -> decode0x5Instruction(instruction, instructionExecutors)
                    0x6 -> instructionExecutors.forEach { it.loadMemoryIntoRegister(instruction) }
                    0x7 -> instructionExecutors.forEach { it.addValueToRegister(instruction) }
                    0x8 -> decode0x8Instruction(instruction, instructionExecutors)
                    0x9 -> decode0x9Instruction(instruction, instructionExecutors)
                    0xA -> instructionExecutors.forEach { it.loadMemoryIntoIRegister(instruction) }
                    0xB -> instructionExecutors.forEach { it.jumpWithOffset(instruction) }
                    0xC -> instructionExecutors.forEach { it.random(instruction) }
                    0xD -> instructionExecutors.forEach { it.draw(instruction) }
                    0xE -> decode0xEInstruction(instruction, instructionExecutors)
                    0xF -> decode0xFInstruction(instruction, instructionExecutors)
                }
            }
        }
    }

    private fun decode0x5Instruction(instruction: UInt, instructionExecutors: List<InstructionExecutor>) {
        if (rightNibble(instruction).toInt() == 0) {
            instructionExecutors.forEach { it.skipIfRegisterAndRegisterEqual(instruction) }
        } else {
            throwException(instruction)
        }
    }

    private fun decode0x8Instruction(instruction: UInt, instructionExecutors: List<InstructionExecutor>) {
        when (rightNibble(instruction).toInt()) {
            0x0 -> instructionExecutors.forEach { it.loadRegisterIntoRegister(instruction) }
            0x1 -> instructionExecutors.forEach { it.or(instruction) }
            0x2 -> instructionExecutors.forEach { it.and(instruction) }
            0x3 -> instructionExecutors.forEach { it.xor(instruction) }
            0x4 -> instructionExecutors.forEach { it.addRegisterAndRegister(instruction) }
            0x5 -> instructionExecutors.forEach { it.subtractYRegisterFromXRegister(instruction) }
            0x6 -> instructionExecutors.forEach { it.shiftRightXOnlyVariant(instruction) }
            0x7 -> instructionExecutors.forEach { it.subtractXRegisterFromYRegister(instruction) }
            0xE -> instructionExecutors.forEach { it.shiftLeftXOnlyVariant(instruction) }
            else -> throwException(instruction)
        }
    }

    private fun decode0x9Instruction(instruction: UInt, instructionExecutors: List<InstructionExecutor>) {
        if (rightNibble(instruction).toInt() == 0) {
            instructionExecutors.forEach { it.skipIfRegisterAndRegisterNotEqual(instruction) }
        } else {
            throwException(instruction)
        }
    }

    private fun decode0xEInstruction(instruction: UInt, instructionExecutors: List<InstructionExecutor>) {
        when(rightByte(instruction).toInt()) {
            0x9E -> instructionExecutors.forEach { it.skipIfKeyPressed(instruction) }
            0xA1 -> instructionExecutors.forEach { it.skipIfKeyNotPressed(instruction) }
            else -> throwException(instruction)
        }
    }

    private fun decode0xFInstruction(instruction: UInt, instructionExecutors: List<InstructionExecutor>) {
        when(rightByte(instruction).toInt()) {
            0x07 -> instructionExecutors.forEach { it.setRegisterToDelayTimerValue(instruction) }
            0x0A -> instructionExecutors.forEach { it.waitForKeyPress(instruction) }
            0x15 -> instructionExecutors.forEach { it.setDelayTimerRegisterToValueInGeneralRegister(instruction) }
            0x18 -> instructionExecutors.forEach { it.setSoundTimerRegisterToValueInGeneralRegister(instruction) }
            0x1E -> instructionExecutors.forEach { it.addGeneralRegisterToIRegister(instruction) }
            0x29 -> instructionExecutors.forEach { it.loadIRegisterWithLocationOfSpriteForDigit(instruction) }
            0x33 -> instructionExecutors.forEach { it.storeBCDRepresentation(instruction) }
            0x55 -> instructionExecutors.forEach { it.loadAllGeneralRegistersIntoMemory(instruction) }
            0x65 -> instructionExecutors.forEach { it.readMemoryIntoAllGeneralRegisters(instruction) }
            else -> throwException(instruction)
        }
    }

    private fun throwException(instruction: UInt): Unit = throw IllegalArgumentException("Unrecognised opcode: ${instruction.toString(16)}")

    companion object {
        private val LOG = logger(this::class)
    }
}