package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.RunningState.*
import com.github.dannyrm.khip8.cpu.InstructionProcessor
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.event.Khip8Event
import com.github.dannyrm.khip8.event.Khip8Observer
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.soywiz.korio.async.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import org.koin.core.annotation.Single

@Single
class Khip8(private val instructionProcessor: InstructionProcessor,
            private val memoryManager: MemoryManager,
            private val display: Display,
            private val delayRegister: TimerRegister,
            private val soundRegister: SoundTimerRegister,
            private val numberOfCpuTicksPerPeripheralTick: Int,
            private val delayBetweenCycles: Long,
            internal var runningState: RunningState) {
    private val observers: MutableList<Khip8Observer> = mutableListOf()

    fun subscribe(khip8Observer: Khip8Observer) {
        observers.add(khip8Observer)
    }

    fun load(rom: ByteArray?) {
        Khip8Status.loadedRom = rom
        reset()
    }

    /*
     * Returns true if the emulator has entered a pause state, false otherwise.
     */
    fun togglePause(): Boolean =
        when (runningState) {
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

        if (memoryManager.loadProgram(Khip8Status.loadedRom)) {
            setSystemState(RUNNING)
        }
    }

    suspend fun execute() {
        val delayPerPeripheralTick = numberOfCpuTicksPerPeripheralTick * delayBetweenCycles

        launch(Dispatchers.Default) {
            while (true) {
                instructionProcessor.tick()
                delay(delayBetweenCycles)
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

    private fun setSystemState(state: RunningState) {
        runningState = state

        observers.forEach {
            it.receiveEvent(Khip8Event(runningState))
        }
    }

    companion object { private val LOG = logger(this::class) }
}
