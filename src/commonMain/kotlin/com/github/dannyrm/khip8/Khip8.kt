package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.util.lineSeparator
import com.github.dannyrm.khip8.util.logger
import com.soywiz.korio.async.runBlockingNoJs
import kotlinx.coroutines.delay
import kotlinx.coroutines.job
import kotlin.time.ExperimentalTime

@OptIn(ExperimentalTime::class)
class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager,
            private val display: Display, private val config: Config, private val ui: Ui) {

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

        val khip8 = this

        runBlockingNoJs {
            ui.start(config, this.coroutineContext.job, khip8)
        }

        LOG.info {"Halting machine..." }
    }

    suspend fun execute(cpuTicksPerPeripheralTick: Int, delayInMillis: Long) {
        for (i in 0 until cpuTicksPerPeripheralTick) {
            cpu.tick()
            delay(delayInMillis)
        }
        memoryManager.delayRegister.tick()
        memoryManager.soundRegister.tick()
    }

    companion object {
        private val LOG = logger(this::class)
    }
}