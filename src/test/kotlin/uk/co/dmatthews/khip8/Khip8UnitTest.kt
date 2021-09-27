package uk.co.dmatthews.khip8

import io.mockk.coVerify
import uk.co.dmatthews.khip8.cpu.Cpu
import io.mockk.mockk
import io.mockk.verify
import uk.co.dmatthews.khip8.memory.MemoryManager
import org.junit.jupiter.api.Test
import java.io.File

class Khip8UnitTest {

    @Test
    fun `Sprite data loaded at startup`() {
        val memoryManager = mockk<MemoryManager>(relaxed = true)
        val cpu = mockk<Cpu>(relaxed = true)

        Khip8(cpu, memoryManager)

        verify { memoryManager.loadSpriteDigitsIntoMemory() }
    }

    @Test
    fun `load loads rom into memory`() {
        val memoryManager = mockk<MemoryManager>(relaxed = true)
        val cpu = mockk<Cpu>(relaxed = true)

        val file = mockk<File>()

        val khip8 = Khip8(cpu, memoryManager)
        khip8.load(file)

        verify { memoryManager.loadProgram(file) }
    }

    @Test
    fun `start starts the cpu and timers`() {
        val memoryManager = mockk<MemoryManager>(relaxed = true)
        val cpu = mockk<Cpu>(relaxed = true)

        val khip8 = Khip8(cpu, memoryManager)
        khip8.start()

        coVerify {
            cpu.start()
            memoryManager.soundRegister.start()
            memoryManager.delayRegister.start()
        }
    }

    @Test
    fun `halt stops the cpu and timers`() {
        val memoryManager = mockk<MemoryManager>(relaxed = true)
        val cpu = mockk<Cpu>(relaxed = true)

        val khip8 = Khip8(cpu, memoryManager)
        khip8.halt()

        coVerify {
            cpu.halt()
            memoryManager.soundRegister.halt()
            memoryManager.delayRegister.halt()
        }
    }
}
