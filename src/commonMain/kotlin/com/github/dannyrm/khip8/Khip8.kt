package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.config.delayBetweenCycles
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.util.lineSeparator
import com.github.dannyrm.khip8.util.logger
import com.github.dannyrm.khip8.util.waitFor

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager,
            private val display: Display, private val config: Config,
            private var halt: Boolean = false) {

    init {
        memoryManager.loadSpriteDigitsIntoMemory()
        LOG.info { "Loaded sprite digits into memory..." }
    }

    fun load(romPath: String) {
        memoryManager.loadProgram(romPath)
        LOG.info {"Program Loaded..." }
    }

    fun start() {
        LOG.debug { "System starting state: ${lineSeparator()}" }
        LOG.debug { memoryManager.toString() }
        LOG.debug { display.toString() }

        val cpuTicksPerPeripheralTick = numberOfCpuTicksPerPeripheralTick()

        val (delayInMillis, delayInNanos) = delayBetweenCycles(config)

        while (!halt) {
            execute(cpuTicksPerPeripheralTick, delayInMillis, delayInNanos)
        }

        LOG.info {"Halting machine..." }
    }

    internal fun execute(cpuTicksPerPeripheralTick: Int, delayInMillis: Long, delayInNanos: Int) {
        for (i in 0 until cpuTicksPerPeripheralTick) {
            cpu.tick()
            waitFor(delayInMillis, delayInNanos)
        }
        memoryManager.delayRegister.tick()
        memoryManager.soundRegister.tick()
        display.tick()
    }

    fun halt() {
        halt = true
    }

    internal fun numberOfCpuTicksPerPeripheralTick(): Int {
        return config.systemSpeedConfig.cpuSpeed / config.systemSpeedConfig.timerSpeed
    }

    companion object {
        private val LOG = logger(this::class)
    }
}