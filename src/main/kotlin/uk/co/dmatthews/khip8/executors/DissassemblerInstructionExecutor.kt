package uk.co.dmatthews.khip8.executors

import rightNibbleByte
import toHexMinimal

class DissassemblerInstructionExecutor(val codeListing: MutableList<String> = mutableListOf()): InstructionExecutor {

    override fun sysCall(value: UInt) {
        codeListing.add("SYS ${toHexMinimal(rightNibbleByte(value))}")
    }

    override fun clearScreen(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun doReturn(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun jump(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun call(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun skipIfRegisterAndMemoryEqual(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun skipIfRegisterAndMemoryNotEqual(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun loadMemoryIntoRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun addValueToRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun loadMemoryIntoIRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun jumpWithOffset(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun random(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun draw(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun skipIfRegisterAndRegisterEqual(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun loadRegisterIntoRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun or(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun and(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun xor(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun addRegisterAndRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun subtractYRegisterFromXRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun shiftRightXOnlyVariant(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun subtractXRegisterFromYRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun shiftLeftXOnlyVariant(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun skipIfRegisterAndRegisterNotEqual(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun skipIfKeyPressed(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun skipIfKeyNotPressed(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun setRegisterToDelayTimerValue(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun waitForKeyPress(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun setDelayTimerRegisterToValueInGeneralRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun setSoundTimerRegisterToValueInGeneralRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun addGeneralRegisterToIRegister(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun loadIRegisterWithLocationOfSpriteForDigit(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun storeBCDRepresentation(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun loadAllGeneralRegistersIntoMemory(value: UInt) {
        TODO("Not yet implemented")
    }

    override fun readMemoryIntoAllGeneralRegisters(value: UInt) {
        TODO("Not yet implemented")
    }
}