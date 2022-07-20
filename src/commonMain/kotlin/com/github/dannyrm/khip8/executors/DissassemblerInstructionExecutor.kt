package com.github.dannyrm.khip8.executors

import rightByte
import rightNibble
import rightNibbleByte
import toHexMinimal
import x
import y

class DissassemblerInstructionExecutor(val codeListing: MutableList<String> = mutableListOf()): InstructionExecutor {

    private fun xKKInstruction(mnemonic: String, value: UInt) {
        codeListing.add("$mnemonic V${toHexMinimal(x(value))}, ${toHexMinimal(rightByte(value))}")
    }

    private fun nNNInstruction(mnemonic: String, value: UInt) {
        codeListing.add("$mnemonic ${toHexMinimal(rightNibbleByte(value))}")
    }

    private fun mnemonicOnlyInstruction(mnemonic: String) {
        codeListing.add(mnemonic)
    }

    private fun xyNInstruction(mnemonic: String, value: UInt) {
        codeListing.add("$mnemonic V${toHexMinimal(x(value))}, V${toHexMinimal(y(value))}, ${
            toHexMinimal(
                rightNibble(
                    value
                )
            )
        }")
    }

    private fun xYInstruction(mnemonic: String, value: UInt) {
        codeListing.add("$mnemonic V${toHexMinimal(x(value))}, V${toHexMinimal(y(value))}")
    }

    private fun xInstruction(mnemonic: String, value: UInt) {
        codeListing.add("$mnemonic V${toHexMinimal(x(value))}")
    }

    private fun wrappedXInstruction(mnemonic: String, value: UInt, suffix: String) {
        codeListing.add("$mnemonic V${toHexMinimal(x(value))}, $suffix")
    }

    override fun executionPaused(): Boolean = false

    // mnemonic only Instructions
    override fun clearScreen(value: UInt) = mnemonicOnlyInstruction("CLS")
    override fun doReturn(value: UInt) = mnemonicOnlyInstruction("RET")

    // nnn instructions
    override fun sysCall(value: UInt) = nNNInstruction("SYS", value)
    override fun jump(value: UInt) = nNNInstruction("JP", value)
    override fun call(value: UInt) = nNNInstruction("CALL", value)
    override fun loadMemoryIntoIRegister(value: UInt) = nNNInstruction("LD I,", value)
    override fun jumpWithOffset(value: UInt) = nNNInstruction("JP V0,", value)

    // x, kk instructions
    override fun skipIfRegisterAndMemoryEqual(value: UInt) = xKKInstruction("SE", value)
    override fun skipIfRegisterAndMemoryNotEqual(value: UInt) = xKKInstruction("SNE", value)
    override fun loadMemoryIntoRegister(value: UInt) = xKKInstruction("LD", value)
    override fun addValueToRegister(value: UInt) = xKKInstruction("ADD", value)
    override fun random(value: UInt) = xKKInstruction("RND", value)

    // x, y, n instructions
    override fun draw(value: UInt) = xyNInstruction("DRW", value)

    // x, y instructions
    override fun skipIfRegisterAndRegisterEqual(value: UInt) = xYInstruction("SE", value)
    override fun loadRegisterIntoRegister(value: UInt) = xYInstruction("LD", value)
    override fun or(value: UInt) = xYInstruction("OR", value)
    override fun and(value: UInt) = xYInstruction("AND", value)
    override fun xor(value: UInt) = xYInstruction("XOR", value)
    override fun addRegisterAndRegister(value: UInt) = xYInstruction("ADD", value)
    override fun subtractYRegisterFromXRegister(value: UInt) = xYInstruction("SUB", value)
    override fun subtractXRegisterFromYRegister(value: UInt) = xYInstruction("SUBN", value)
    override fun skipIfRegisterAndRegisterNotEqual(value: UInt) = xYInstruction("SNE", value)

    // x instructions
    override fun shiftRightXOnlyVariant(value: UInt) = xInstruction("SHR", value)
    override fun shiftLeftXOnlyVariant(value: UInt) = xInstruction("SHL", value)
    override fun skipIfKeyPressed(value: UInt) = xInstruction("SKP", value)
    override fun skipIfKeyNotPressed(value: UInt) = xInstruction("SKNP", value)
    override fun setDelayTimerRegisterToValueInGeneralRegister(value: UInt) = xInstruction("LD DT,", value)
    override fun setSoundTimerRegisterToValueInGeneralRegister(value: UInt) = xInstruction("LD ST,", value)
    override fun addGeneralRegisterToIRegister(value: UInt) = xInstruction("ADD I,", value)
    override fun loadIRegisterWithLocationOfSpriteForDigit(value: UInt) = xInstruction("LD F,", value)
    override fun storeBCDRepresentation(value: UInt) = xInstruction("LD B,", value)
    override fun loadAllGeneralRegistersIntoMemory(value: UInt) = xInstruction("LD [I],", value)

    // Wrapped x instructions
    override fun setRegisterToDelayTimerValue(value: UInt) = wrappedXInstruction("LD", value, "DT")
    override fun waitForKeyPress(value: UInt) = wrappedXInstruction("LD", value, "K")
    override fun readMemoryIntoAllGeneralRegisters(value: UInt) = wrappedXInstruction("LD", value, "[I]")
}