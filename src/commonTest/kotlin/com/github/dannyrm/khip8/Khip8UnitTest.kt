package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.Khip8State.*
import com.github.dannyrm.khip8.config.*
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.github.dannyrm.khip8.test.utils.BaseTest
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import com.github.dannyrm.khip8.util.SystemMode
import io.mockk.*
import kotlinx.coroutines.test.*
import kotlin.test.Test
import kotlin.test.expect

class Khip8UnitTest: BaseTest() {

    @MockK(relaxed = true) private lateinit var memoryManager: MemoryManager
    @MockK(relaxed = true) private lateinit var cpu: Cpu
    @MockK(relaxed = true) private lateinit var display: Display
    @MockK(relaxed = true) private lateinit var delayRegister: TimerRegister
    @MockK(relaxed = true) private lateinit var soundRegister: SoundTimerRegister
    @MockK(relaxed = true) private lateinit var khip8Status: Khip8Status

    private var config: Config = Config(
        systemSpeedConfig = SystemSpeedConfig(cpuSpeed = 540, timerSpeed = 60, displayRefreshRate = 60),
        frontEndConfig = FrontEndConfig(windowWidth = 512, windowHeight = 256),
        systemMode = SystemMode.SUPER_CHIP_MODE,
        memoryConfig = MemoryConfig(
            memorySize = 4096,
            stackSize = 16,
            interpreterStartAddress = 0x0,
            programStartAddress = 0x200
        )
    )

    @InjectMockKs
    private lateinit var khip8: Khip8

    @Test
    fun `load loads rom into memory`() {
        val file = byteArrayOf()

        every { khip8Status.loadedRom } returns file
        every { memoryManager.loadProgram(file) } returns true

        khip8.load(file)

        verify { khip8Status.loadedRom = file }

        verify { display.clear() }
        verify { memoryManager.resetMemory() }
        verify { memoryManager.loadProgram(file) }

        verifyOrder {
            // Initially the machine will be empty and paused
            khip8Status.khip8State = STOPPED
            cpu.cpuState = STOPPED
            delayRegister.state = STOPPED
            soundRegister.state = STOPPED

            // Once the Rom is successfully loaded we indicate this and unpause
            khip8Status.khip8State = RUNNING
            cpu.cpuState = RUNNING
            delayRegister.state = RUNNING
            soundRegister.state = RUNNING
        }
    }

    @Test
    fun `Reset clears state`() {
        val file = byteArrayOf()

        every { khip8Status.loadedRom } returns file
        every { memoryManager.loadProgram(file) } returns true

        khip8.reset()

        verify { display.clear() }
        verify { memoryManager.resetMemory() }

        verifyOrder {
            // Initially the machine will be empty and stopped
            khip8Status.khip8State = STOPPED
            cpu.cpuState = STOPPED
            delayRegister.state = STOPPED
            soundRegister.state = STOPPED

            // Once the Rom is successfully loaded we indicate this and unpause
            khip8Status.khip8State = RUNNING
            cpu.cpuState = RUNNING
            delayRegister.state = RUNNING
            soundRegister.state = RUNNING
        }
    }

    @Test
    fun `Reset clears state and leaves the emulator paused if rom does not load`() {
        val file = byteArrayOf()

        every { khip8Status.loadedRom } returns file
        every { memoryManager.loadProgram(file) } returns false

        khip8.reset()

        verify { display.clear() }
        verify { memoryManager.resetMemory() }

        verifyOrder {
            khip8Status.khip8State = STOPPED
            cpu.cpuState = STOPPED
            delayRegister.state = STOPPED
            soundRegister.state = STOPPED
        }

        verifyAll(inverse = true) {
            khip8Status.khip8State = RUNNING
            cpu.cpuState = RUNNING
            delayRegister.state = RUNNING
            soundRegister.state = RUNNING
        }
    }

    @Test
    fun `Check number of Cpu ticks per peripheral tick`() {
        expect(9) { numberOfCpuTicksPerPeripheralTick(config) }
    }

    @Test
    fun `Check delay between Cpu ticks`() {
        expect(2L) { delayBetweenCycles(config) }
    }
}