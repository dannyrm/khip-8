package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.RunningState.*
import com.github.dannyrm.khip8.cpu.InstructionProcessor
import com.github.dannyrm.khip8.observers.RomStateEvent
import com.github.dannyrm.khip8.observers.RomStateObserver
import com.github.dannyrm.khip8.observers.RomStatus
import com.github.dannyrm.khip8.observers.SystemStateObserver
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.soywiz.korio.async.launch
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlin.properties.Delegates

/*
 * Responsible for starting the emulated units and maintaining two pieces of state:
 * * The running status of the machine (running, stopped or paused)
 * * The ROM data being executed by the machine.
 *
 * Events are generated and distributed to any observers whenever the running status or ROM data is changed.
 */
class Khip8(private val instructionProcessor: InstructionProcessor,
            private val delayRegister: TimerRegister,
            private val soundRegister: SoundTimerRegister,
            private val numberOfCpuTicksPerPeripheralTick: Int,
            private val delayBetweenCycles: Long) {
    private val systemStatusObservers: MutableList<SystemStateObserver> = mutableListOf()
    private val romStatusObservers: MutableList<RomStateObserver> = mutableListOf()

    internal var runningState: RunningState by Delegates.observable(STOPPED) { _, _, newValue ->
        systemStatusObservers.forEach {
            it.receiveEvent(newValue)
        }
    }

    internal var loadedRom: ByteArray? by Delegates.observable(null) { _, oldValue, newValue ->
        if (!oldValue.contentEquals(newValue)) {

            val romStatus =
                if (oldValue == null && newValue != null) {
                    RomStatus.LOADED
                } else if (oldValue != null && newValue == null) {
                    RomStatus.UNLOADED
                } else { // Rom contents must have changed from one rom to another
                    RomStatus.LOADED
                }

            romStatusObservers.forEach {
                it.receiveEvent(RomStateEvent(newValue, romStatus))
            }

            runningState = when (romStatus) {
                RomStatus.LOADED -> {
                    RUNNING
                }
                RomStatus.UNLOADED -> {
                    STOPPED
                }
            }
        }
    }

    fun subscribe(systemStateObserver: SystemStateObserver) {
        systemStatusObservers.add(systemStateObserver)
    }

    fun subscribe(romStateObserver: RomStateObserver) {
        romStatusObservers.add(romStateObserver)
    }

    fun load(rom: ByteArray?) {
        loadedRom = rom
    }

    /*
     * Returns true if the emulator has entered a pause state, false otherwise.
     */
    fun togglePause(): Boolean =
        when (runningState) {
            RUNNING -> {
                runningState = PAUSED

                true
            }
            PAUSED -> {
                runningState = RUNNING

                false
            }
            STOPPED -> {
                LOG.info { "Emulator not running so no need to pause" }
                false
            }
        }

    suspend fun execute() {
        runningState = STOPPED

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

    companion object { private val LOG = logger(this::class) }
}
