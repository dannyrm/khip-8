package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.config.*
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.CpuState
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.memory.TimerRegisterState
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
        soundConfig = SoundConfig(midiInstrumentNumber = 0, midiNoteNumber = 0, midiNoteVelocity = 0),
        frontEndConfig = FrontEndConfig(FrontEndType.JAVA_AWT, windowWidth = 512, windowHeight = 256),
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
        val filePath = "the-file-path"

        every { khip8Status.loadedRomPath } returns filePath
        every { memoryManager.loadProgram(filePath) } returns true

        khip8.load(filePath)

        verify { khip8Status.loadedRomPath = filePath }

        verify { delayRegister.clear() }
        verify { soundRegister.clear() }
        verify { display.clear() }
        verify { memoryManager.resetMemory() }
        verify { memoryManager.loadProgram(filePath) }

        verifyOrder {
            // Initially the machine will be empty and paused
            khip8Status.khip8State = Khip8State.EMPTY
            cpu.cpuState = CpuState.PAUSED
            delayRegister.state = TimerRegisterState.PAUSED
            soundRegister.state = TimerRegisterState.PAUSED

            // Once the Rom is successfully loaded we indicate this and unpause
            khip8Status.khip8State = Khip8State.LOADED
            cpu.cpuState = CpuState.RUNNING
            delayRegister.state = TimerRegisterState.RUNNING
            soundRegister.state = TimerRegisterState.RUNNING
        }
    }

    @Test
    fun `Reset clears state`() {
        val filePath = "the-file-path"

        every { khip8Status.loadedRomPath } returns filePath
        every { memoryManager.loadProgram(filePath) } returns true

        khip8.reset()

        verify { delayRegister.clear() }
        verify { soundRegister.clear() }
        verify { display.clear() }
        verify { memoryManager.resetMemory() }

        verifyOrder {
            // Initially the machine will be empty and paused
            khip8Status.khip8State = Khip8State.EMPTY
            cpu.cpuState = CpuState.PAUSED
            delayRegister.state = TimerRegisterState.PAUSED
            soundRegister.state = TimerRegisterState.PAUSED

            // Once the Rom is successfully loaded we indicate this and unpause
            khip8Status.khip8State = Khip8State.LOADED
            cpu.cpuState = CpuState.RUNNING
            delayRegister.state = TimerRegisterState.RUNNING
            soundRegister.state = TimerRegisterState.RUNNING
        }
    }

    @Test
    fun `Reset clears state and leaves the emulator paused if rom does not load`() {
        val filePath = "the-file-path"

        every { khip8Status.loadedRomPath } returns filePath
        every { memoryManager.loadProgram(filePath) } returns false

        khip8.reset()

        verify { delayRegister.clear() }
        verify { soundRegister.clear() }
        verify { display.clear() }
        verify { memoryManager.resetMemory() }

        verifyOrder {
            khip8Status.khip8State = Khip8State.EMPTY
            cpu.cpuState = CpuState.PAUSED
            delayRegister.state = TimerRegisterState.PAUSED
            soundRegister.state = TimerRegisterState.PAUSED
        }

        verifyAll(inverse = true) {
            khip8Status.khip8State = Khip8State.LOADED
            cpu.cpuState = CpuState.RUNNING
            delayRegister.state = TimerRegisterState.RUNNING
            soundRegister.state = TimerRegisterState.RUNNING
        }
    }

    @Test
    fun `Check number of Cpu ticks per peripheral tick`() {
        expect(9) { numberOfCpuTicksPerPeripheralTick(config) }
    }

    @Test
    fun `Check delay between Cpu ticks`() {
        expect(Pair(1L, 851851)) { delayBetweenCycles(config) }
    }

    @Test
    fun `Check delay between Cpu ticks when a small number of nanos`() {
        expect(Pair(1L, 25)) {
            delayBetweenCycles(
                    config.copy(systemSpeedConfig = SystemSpeedConfig(cpuSpeed = 800, timerSpeed = 60, displayRefreshRate = 60))
            )
        }
    }

    @Test
    fun `Check delay between Cpu ticks when no nanos`() {
        expect(Pair(1L, 0)) {
            delayBetweenCycles(
                    config.copy(systemSpeedConfig = SystemSpeedConfig(cpuSpeed = 1000, timerSpeed = 60, displayRefreshRate = 60))
            )
        }
    }

    @Test
    fun `Execute works correctly`() {
        runTest {
            khip8.execute(15, 0)

            verify(exactly = 15) { cpu.tick() }
            verify { delayRegister.tick() }
            verify { soundRegister.tick() }

            confirmVerified(cpu, delayRegister, soundRegister, display)
        }
    }
}