package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.CpuState
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.memory.TimerRegisterState
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.github.dannyrm.khip8.util.logger
import kotlinx.coroutines.delay

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager, private val display: Display,
            private val delayRegister: TimerRegister, private val soundRegister: SoundTimerRegister,
            private val chip8InputManager: Chip8InputManager, private var khip8Status: Khip8Status) {


    fun load(rom: ByteArray?) {
        khip8Status.loadedRom = rom
        reset()
    }

    /*
     * Returns true if the emulator has entered a pause state, false otherwise.
     */
    fun togglePause(): Boolean {
        // If the emulator isn't running we don't need to pause.
        if (khip8Status.khip8State == Khip8State.EMPTY) {
            LOG.info { "Emulator not running so no need to pause" }
            return false
        }

        return if (cpu.cpuState == CpuState.RUNNING) {
            cpu.cpuState = CpuState.PAUSED
            delayRegister.state = TimerRegisterState.PAUSED
            soundRegister.state = TimerRegisterState.PAUSED

            logSystemState()

            true
        } else {
            cpu.cpuState = CpuState.RUNNING
            delayRegister.state = TimerRegisterState.RUNNING
            soundRegister.state = TimerRegisterState.RUNNING

            logSystemState()

            false
        }
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

        if (memoryManager.loadProgram(khip8Status.loadedRom)) {
            khip8Status.khip8State = Khip8State.LOADED
            cpu.cpuState = CpuState.RUNNING
            delayRegister.state = TimerRegisterState.RUNNING
            soundRegister.state = TimerRegisterState.RUNNING
        }

        logSystemState()
    }

    suspend fun execute(cpuTicksPerPeripheralTick: Int, delayInMillis: Long) {
        for (i in 0 until cpuTicksPerPeripheralTick) {
            cpu.tick()
            delay(delayInMillis)
        }
        delayRegister.tick()
        soundRegister.tick()
    }

    private fun logSystemState() {
        LOG.info { "System State: { Console: ${khip8Status.khip8State}, CPU: ${cpu.cpuState}, Delay Timer: ${delayRegister.state}, Sound Timer: ${soundRegister.state} }" }
    }

    companion object { private val LOG = logger(this::class) }
}
