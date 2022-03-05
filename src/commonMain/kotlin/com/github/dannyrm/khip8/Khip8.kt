package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.CpuState
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.memory.TimerRegisterState
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.github.dannyrm.khip8.util.logger
import kotlinx.coroutines.delay

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager, private val display: Display,
            private val delayRegister: TimerRegister, private val soundRegister: SoundTimerRegister,
            private var khip8Status: Khip8Status) {


    fun load(romPath: String?) {
        khip8Status.loadedRomPath = romPath
        reset()
    }

    fun reset() {
        khip8Status.khip8State = Khip8State.EMPTY
        cpu.cpuState = CpuState.PAUSED
        delayRegister.state = TimerRegisterState.PAUSED
        soundRegister.state = TimerRegisterState.PAUSED

        delayRegister.clear()
        soundRegister.clear()
        display.clear()

        memoryManager.resetMemory()

        if (memoryManager.loadProgram(khip8Status.loadedRomPath)) {
            khip8Status.khip8State = Khip8State.LOADED
            cpu.cpuState = CpuState.RUNNING
            delayRegister.state = TimerRegisterState.RUNNING
            soundRegister.state = TimerRegisterState.RUNNING
        }

        LOG.info { "System reset. State: { Console: ${khip8Status.khip8State}, CPU: ${cpu.cpuState}, Delay Timer: ${delayRegister.state}, Sound Timer: ${soundRegister.state} }" }
    }

    suspend fun execute(cpuTicksPerPeripheralTick: Int, delayInMillis: Long) {
        for (i in 0 until cpuTicksPerPeripheralTick) {
            cpu.tick()
            delay(delayInMillis)
        }
        delayRegister.tick()
        soundRegister.tick()
    }

    companion object { private val LOG = logger(this::class) }
}
