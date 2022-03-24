package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.Khip8State.*
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.input.InputEvent
import com.github.dannyrm.khip8.input.InputObserver
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.soywiz.korio.async.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager, private val display: Display,
            private val delayRegister: TimerRegister, private val soundRegister: SoundTimerRegister,
            private var khip8Status: Khip8Status): InputObserver {


    fun load(rom: ByteArray?) {
        khip8Status.loadedRom = rom
        reset()
    }

    override fun receiveEvent(inputEvent: InputEvent) {
        // We only want to unpause the CPU if the emulator is running.
        if (inputEvent.isActive && khip8Status.khip8State == RUNNING) {
            cpu.cpuState = RUNNING
            logSystemState()
        }
    }

    /*
     * Returns true if the emulator has entered a pause state, false otherwise.
     */
    fun togglePause(): Boolean =
        when (khip8Status.khip8State) {
            RUNNING -> {
                setSystemState(PAUSED)

                true
            }
            PAUSED -> {
                setSystemState(RUNNING)

                false
            }
            STOPPED -> {
                LOG.info { "Emulator not running so no need to pause" }
                false
            }
        }

    fun reset() {
        setSystemState(STOPPED)

        display.clear()
        memoryManager.resetMemory()

        if (memoryManager.loadProgram(khip8Status.loadedRom)) {
            setSystemState(RUNNING)
        }
    }

    suspend fun execute(cpuTicksPerPeripheralTick: Int, delayInMillis: Long) {
        val delayPerPeripheralTick = cpuTicksPerPeripheralTick * delayInMillis

        launch(Dispatchers.Default) {
            while (true) {
                cpu.tick()
                delay(delayInMillis)
            }
        }

        launch(Dispatchers.Default) {
            while (true) {
                delay(delayPerPeripheralTick)
                delayRegister.tick()
            }
        }

        launch(Dispatchers.Default) {
            while (true) {
                delay(delayPerPeripheralTick)
                soundRegister.tick()
            }
        }
    }

    private fun setSystemState(state: Khip8State) {
        khip8Status.khip8State = state

        cpu.cpuState = state
        delayRegister.state = state
        soundRegister.state = state

        logSystemState()
    }

    private fun logSystemState() {
        LOG.debug { "System State: { Console: ${khip8Status.khip8State}, CPU: ${cpu.cpuState}, Delay Timer: ${delayRegister.state}, Sound Timer: ${soundRegister.state} }" }
    }

    companion object { private val LOG = logger(this::class) }
}
