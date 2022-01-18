package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.config.*
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.test.utils.BaseTest
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import com.github.dannyrm.khip8.util.SystemMode
import io.mockk.*
import kotlin.test.Test
import kotlin.test.expect

class Khip8UnitTest: BaseTest() {

    @MockK(relaxed = true) private lateinit var memoryManager: MemoryManager
    @MockK(relaxed = true) private lateinit var cpu: Cpu
    @MockK(relaxed = true) private lateinit var display: Display
    private var config: Config = Config(
        systemSpeedConfig = SystemSpeedConfig(cpuSpeed = 540, timerSpeed = 60, displayRefreshRate = 60),
        soundConfig = SoundConfig(midiInstrumentNumber = 0, midiNoteNumber = 0, midiNoteVelocity = 0),
        frontEndConfig = FrontEndConfig(FrontEndType.JAVA_AWT),
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
    fun `Sprite data loaded at startup`() {
        verify { memoryManager.loadSpriteDigitsIntoMemory() }
    }

    @Test
    fun `load loads rom into memory`() {
        val filePath = "the-file-path"
        khip8.load(filePath)

        verify { memoryManager.loadProgram(filePath) }
    }

    @Test
    fun `Halt works correctly`() {
        khip8.halt()

        khip8.start()

        verify(inverse = true) { cpu.tick() }
    }

    @Test
    fun `Check number of Cpu ticks per peripheral tick`() {
        expect(9) { khip8.numberOfCpuTicksPerPeripheralTick() }
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
        val delayRegister = mockk<TimerRegister>(relaxed = true)
        val soundRegister = mockk<TimerRegister>(relaxed = true)

        every { memoryManager.delayRegister } returns delayRegister
        every { memoryManager.soundRegister } returns soundRegister

        khip8.execute(15, 0,0)

        verify(exactly = 15) { cpu.tick() }
        verify { delayRegister.tick() }
        verify { soundRegister.tick() }
        verify { display.tick() }

        confirmVerified(cpu, delayRegister, soundRegister, display)
    }
}