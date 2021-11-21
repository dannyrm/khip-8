package uk.co.dmatthews.khip8

import io.mockk.*
import uk.co.dmatthews.khip8.display.model.Display
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import uk.co.dmatthews.khip8.cpu.Cpu
import uk.co.dmatthews.khip8.memory.MemoryManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import uk.co.dmatthews.khip8.input.SystemActionInputManager
import uk.co.dmatthews.khip8.memory.TimerRegister
import java.io.File

@ExtendWith(MockKExtension::class)
class Khip8UnitTest {

    @MockK(relaxed = true) private lateinit var memoryManager: MemoryManager
    @MockK(relaxed = true) private lateinit var cpu: Cpu
    @MockK(relaxed = true) private lateinit var display: Display
    @MockK(relaxed = true) private lateinit var systemActionInputManager: SystemActionInputManager

    @InjectMockKs private lateinit var khip8: Khip8

    @Test
    fun `Sprite data loaded at startup`() {
        verify { memoryManager.loadSpriteDigitsIntoMemory() }
    }

    @Test
    fun `load loads rom into memory`() {
        val file = mockk<File>()
        khip8.load(file)

        verify { memoryManager.loadProgram(file) }
    }

    @Test
    fun `Halt works correctly`() {
        khip8.halt()

        khip8.start()

        verify(inverse = true) { cpu.tick() }
    }

    @Test
    fun `Start initialises display`() {
        khip8.halt()

        khip8.start()

        verify { display.init(khip8::halt) }
    }

    @Test
    fun `Check number of Cpu ticks per peripheral tick`() {
        expectThat(khip8.numberOfCpuTicksPerPeripheralTick()).isEqualTo(9)
    }

    @Test
    fun `Check delay between Cpu ticks`() {
        expectThat(khip8.delayBetweenCycles()).isEqualTo(listOf<Number>(1L, 851851))
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
