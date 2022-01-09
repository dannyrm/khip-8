package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.config.*
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import com.github.dannyrm.khip8.util.SystemMode
import java.io.File

@ExtendWith(MockKExtension::class)
class Khip8UnitTest {

    @MockK(relaxed = true) private lateinit var memoryManager: MemoryManager
    @MockK(relaxed = true) private lateinit var cpu: Cpu
    @MockK(relaxed = true) private lateinit var display: Display
    private var config: Config = Config(
        SystemSpeedConfig(cpuSpeed = 540, timerSpeed = 60, displayRefreshRate = 60),
        SoundConfig(midiInstrumentNumber = 0, midiNoteNumber = 0, midiNoteVelocity = 0),
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
        expectThat(khip8.numberOfCpuTicksPerPeripheralTick()).isEqualTo(9)
    }

    @Test
    fun `Check delay between Cpu ticks`() {
        expectThat(delayBetweenCycles(config)).isEqualTo(Pair(1L, 851851))
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