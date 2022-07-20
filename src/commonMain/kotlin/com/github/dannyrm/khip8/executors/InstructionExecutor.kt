package com.github.dannyrm.khip8.executors

interface InstructionExecutor {
    fun executionPaused(): Boolean

    fun sysCall(value: UInt)
    fun clearScreen(value: UInt)
    fun doReturn(value: UInt)
    fun jump(value: UInt)
    fun call(value: UInt)
    fun skipIfRegisterAndMemoryEqual(value: UInt)
    fun skipIfRegisterAndMemoryNotEqual(value: UInt)
    fun loadMemoryIntoRegister(value: UInt)
    fun addValueToRegister(value: UInt)
    fun loadMemoryIntoIRegister(value: UInt)
    fun jumpWithOffset(value: UInt)
    fun random(value: UInt)
    fun draw(value: UInt)
    fun skipIfRegisterAndRegisterEqual(value: UInt)
    fun loadRegisterIntoRegister(value: UInt)
    fun or(value: UInt)
    fun and(value: UInt)
    fun xor(value: UInt)
    fun addRegisterAndRegister(value: UInt)
    fun subtractYRegisterFromXRegister(value: UInt)
    fun shiftRightXOnlyVariant(value: UInt)
    fun subtractXRegisterFromYRegister(value: UInt)
    fun shiftLeftXOnlyVariant(value: UInt)
    fun skipIfRegisterAndRegisterNotEqual(value: UInt)
    fun skipIfKeyPressed(value: UInt)
    fun skipIfKeyNotPressed(value: UInt)
    fun setRegisterToDelayTimerValue(value: UInt)
    fun waitForKeyPress(value: UInt)
    fun setDelayTimerRegisterToValueInGeneralRegister(value: UInt)
    fun setSoundTimerRegisterToValueInGeneralRegister(value: UInt)
    fun addGeneralRegisterToIRegister(value: UInt)
    fun loadIRegisterWithLocationOfSpriteForDigit(value: UInt)
    fun storeBCDRepresentation(value: UInt)
    fun loadAllGeneralRegistersIntoMemory(value: UInt)
    fun readMemoryIntoAllGeneralRegisters(value: UInt)
}