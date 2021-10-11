package uk.co.dmatthews.khip8

import uk.co.dmatthews.khip8.display.Display
import io.mockk.coVerify
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import uk.co.dmatthews.khip8.cpu.Cpu
import io.mockk.mockk
import io.mockk.verify
import uk.co.dmatthews.khip8.memory.MemoryManager
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.co.dmatthews.khip8.input.SystemActionInputManager
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
    fun `start begins the cpu display and timers`() {
        khip8.start()

        coVerify {
            cpu.start()
            memoryManager.soundRegister.start()
            memoryManager.delayRegister.start()
            display.start(khip8::halt)
        }
    }

    @Test
    fun `halt stops the cpu and timers`() {
        khip8.halt()

        coVerify {
            cpu.halt()
            memoryManager.soundRegister.halt()
            memoryManager.delayRegister.halt()
        }
    }
}
