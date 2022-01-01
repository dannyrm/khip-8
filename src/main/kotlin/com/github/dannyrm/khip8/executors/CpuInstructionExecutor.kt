package com.github.dannyrm.khip8.executors

import com.github.dannyrm.khip8.cpu.Cpu

class CpuInstructionExecutor: InstructionExecutor {
    private lateinit var cpu: Cpu

    fun init(cpu: Cpu) {
        this.cpu = cpu
    }

    override fun sysCall(value: UInt) = cpu.sysCall(value)
    override fun clearScreen(value: UInt) = cpu.clearScreen(value)
    override fun doReturn(value: UInt) = cpu.doReturn(value)
    override fun jump(value: UInt) = cpu.jump(value)
    override fun call(value: UInt) = cpu.call(value)
    override fun skipIfRegisterAndMemoryEqual(value: UInt) = cpu.skipIfRegisterAndMemoryEqual(value)
    override fun skipIfRegisterAndMemoryNotEqual(value: UInt) = cpu.skipIfRegisterAndMemoryNotEqual(value)
    override fun loadMemoryIntoRegister(value: UInt) = cpu.loadMemoryIntoRegister(value)
    override fun addValueToRegister(value: UInt) = cpu.addValueToRegister(value)
    override fun loadMemoryIntoIRegister(value: UInt) = cpu.loadMemoryIntoIRegister(value)
    override fun jumpWithOffset(value: UInt) = cpu.jumpWithOffset(value)
    override fun random(value: UInt) = cpu.random(value)
    override fun draw(value: UInt) = cpu.draw(value)
    override fun skipIfRegisterAndRegisterEqual(value: UInt) = cpu.skipIfRegisterAndRegisterEqual(value)
    override fun loadRegisterIntoRegister(value: UInt) = cpu.loadRegisterIntoRegister(value)
    override fun or(value: UInt) = cpu.or(value)
    override fun and(value: UInt) = cpu.and(value)
    override fun xor(value: UInt) = cpu.xor(value)
    override fun addRegisterAndRegister(value: UInt) = cpu.addRegisterAndRegister(value)
    override fun subtractYRegisterFromXRegister(value: UInt) = cpu.subtractYRegisterFromXRegister(value)
    override fun shiftRightXOnlyVariant(value: UInt) = cpu.shiftRightXOnlyVariant(value)
    override fun subtractXRegisterFromYRegister(value: UInt) = cpu.subtractXRegisterFromYRegister(value)
    override fun shiftLeftXOnlyVariant(value: UInt) = cpu.shiftLeftXOnlyVariant(value)
    override fun skipIfRegisterAndRegisterNotEqual(value: UInt) = cpu.skipIfRegisterAndRegisterNotEqual(value)
    override fun skipIfKeyPressed(value: UInt) = cpu.skipIfKeyPressed(value)
    override fun skipIfKeyNotPressed(value: UInt) = cpu.skipIfKeyNotPressed(value)
    override fun setRegisterToDelayTimerValue(value: UInt) = cpu.setRegisterToDelayTimerValue(value)
    override fun waitForKeyPress(value: UInt) = cpu.waitForKeyPress(value)
    override fun setDelayTimerRegisterToValueInGeneralRegister(value: UInt) = cpu.setDelayTimerRegisterToValueInGeneralRegister(value)
    override fun setSoundTimerRegisterToValueInGeneralRegister(value: UInt) = cpu.setSoundTimerRegisterToValueInGeneralRegister(value)
    override fun addGeneralRegisterToIRegister(value: UInt) = cpu.addGeneralRegisterToIRegister(value)
    override fun loadIRegisterWithLocationOfSpriteForDigit(value: UInt) = cpu.loadIRegisterWithLocationOfSpriteForDigit(value)
    override fun storeBCDRepresentation(value: UInt) = cpu.storeBCDRepresentation(value)
    override fun loadAllGeneralRegistersIntoMemory(value: UInt) = cpu.loadAllGeneralRegistersIntoMemory(value)
    override fun readMemoryIntoAllGeneralRegisters(value: UInt) = cpu.readMemoryIntoAllGeneralRegisters(value)
}