package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.util.logger
import kotlinx.coroutines.delay

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager) {

    fun load(romPath: String) {
        memoryManager.loadProgram(romPath)
        LOG.info {"Program Loaded..." }
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