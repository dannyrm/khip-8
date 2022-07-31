package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.RunningState.*
import com.github.dannyrm.khip8.config.DefaultConfig
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import io.mockk.*
import kotlin.test.Test
import kotlin.test.expect

class Khip8UnitTest {

    @Test
    fun `load loads rom into memory`() {
        val memoryManager = mockk<MemoryManager>(relaxed = true)
        val display = mockk<Display>(relaxed = true)
        val delayRegister = mockk<TimerRegister>(relaxed = true)
        val soundRegister = mockk<SoundTimerRegister>(relaxed = true)

        val khip8 = Khip8(
            instructionProcessor = mockk(),
            memoryManager = memoryManager,
            display = display,
            delayRegister = delayRegister,
            soundRegister = soundRegister,
            numberOfCpuTicksPerPeripheralTick = 1,
            delayBetweenCycles = 1,
            initialRunningState =  RUNNING
        )

        khip8.subscribe(delayRegister)
        khip8.subscribe(soundRegister)

        val file = byteArrayOf()

        every { memoryManager.loadProgram(file) } returns true

        khip8.load(file)

        verify { khip8.loadedRom = file }

        verify { display.clear() }
        verify { memoryManager.resetMemory() }
        verify { memoryManager.loadProgram(file) }

        verifyOrder {
            // Initially the machine will be empty and paused
            khip8.runningState = STOPPED
            // Notify all observers of the state change
            delayRegister.receiveEvent(STOPPED)
            soundRegister.receiveEvent(STOPPED)

            // Once the Rom is successfully loaded we indicate this and unpause
            khip8.runningState = RUNNING
            // Notify all observers of the state change
            delayRegister.receiveEvent(RUNNING)
            soundRegister.receiveEvent(RUNNING)
        }
    }

    @Test
    fun `Reset clears state`() {
        val memoryManager = mockk<MemoryManager>(relaxed = true)
        val display = mockk<Display>(relaxed = true)
        val delayRegister = mockk<TimerRegister>(relaxed = true)
        val soundRegister = mockk<SoundTimerRegister>(relaxed = true)

        val khip8 = Khip8(
            instructionProcessor = mockk(),
            memoryManager = memoryManager,
            display = display,
            delayRegister = delayRegister,
            soundRegister = soundRegister,
            numberOfCpuTicksPerPeripheralTick = 5,
            delayBetweenCycles = 1,
            initialRunningState =  RUNNING
        )

        khip8.subscribe(delayRegister)
        khip8.subscribe(soundRegister)

        every { memoryManager.loadProgram(any()) } returns true

        khip8.reset()

        verify { display.clear() }
        verify { memoryManager.resetMemory() }

        verifyOrder {
            // Initially the machine will be empty and paused
            khip8.runningState = STOPPED
            // Notify all observers of the state change
            delayRegister.receiveEvent(STOPPED)
            soundRegister.receiveEvent(STOPPED)

            // Once the Rom is successfully loaded we indicate this and unpause
            khip8.runningState = RUNNING
            // Notify all observers of the state change
            delayRegister.receiveEvent(RUNNING)
            soundRegister.receiveEvent(RUNNING)
        }
    }

    @Test
    fun `Reset clears state and leaves the emulator paused if rom does not load`() {
        val file = byteArrayOf()

        val memoryManager = mockk<MemoryManager>(relaxed = true)
        val display = mockk<Display>(relaxed = true)
        val delayRegister = mockk<TimerRegister>(relaxed = true)
        val soundRegister = mockk<SoundTimerRegister>(relaxed = true)

        val khip8 = Khip8(
            instructionProcessor = mockk(),
            memoryManager = memoryManager,
            display = display,
            delayRegister = delayRegister,
            soundRegister = soundRegister,
            numberOfCpuTicksPerPeripheralTick = 1,
            delayBetweenCycles = 1,
            initialRunningState =  RUNNING
        )

        khip8.subscribe(delayRegister)
        khip8.subscribe(soundRegister)

        every { khip8.loadedRom } returns file
        every { memoryManager.loadProgram(file) } returns false

        khip8.reset()

        verify { display.clear() }
        verify { memoryManager.resetMemory() }

        verifyOrder {
            khip8.runningState = STOPPED
            // Notify all observers of the state change
            delayRegister.receiveEvent(STOPPED)
            soundRegister.receiveEvent(STOPPED)
        }

        verifyAll(inverse = true) {
            khip8.runningState = RUNNING
            // Observers will not be notified as the rom didn't load
            delayRegister.receiveEvent(RUNNING)
            soundRegister.receiveEvent(RUNNING)
        }
    }

    @Test
    fun `Check number of Cpu ticks per peripheral tick`() {
        expect(9) { DefaultConfig.numberOfCpuTicksPerPeripheralTick() }
    }

    @Test
    fun `Check delay between Cpu ticks`() {
        expect(2L) { DefaultConfig.delayBetweenCycles() }
    }
}