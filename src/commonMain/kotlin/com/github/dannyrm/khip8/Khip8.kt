package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.CpuState
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.util.logger
import kotlinx.coroutines.delay

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager, private val display: Display,
            private var khip8State: Khip8State = Khip8State.EMPTY, private var loadedRomPath: String? = null) {


    fun load(romPath: String) {
        loadedRomPath =  romPath
        reset()
    }

    fun reset() {
        LOG.info { "***** Resetting system" }

        cpu.cpuState = CpuState.PAUSED
        LOG.info { "CPU State: ${cpu.cpuState}" }

        display.clear()
        LOG.info { "Cleared display..." }

        memoryManager.loadProgram(loadedRomPath)

        khip8State = if (loadedRomPath != null) Khip8State.LOADED else Khip8State.EMPTY

        LOG.info {"Console State: $khip8State" }

        cpu.cpuState = CpuState.RUNNING
        LOG.info { "CPU State: ${cpu.cpuState}" }

        LOG.info { "***** System reset successfully" }
    }

    suspend fun execute(cpuTicksPerPeripheralTick: Int, delayInMillis: Long) {
        for (i in 0 until cpuTicksPerPeripheralTick) {
            cpu.tick()
            delay(delayInMillis)
        }
        memoryManager.delayRegister.tick()
        memoryManager.soundRegister.tick()
    }

    companion object { private val LOG = logger(this::class) }
}