package uk.co.dmatthews.khip8.cpu

import io.mockk.*
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import uk.co.dmatthews.khip8.memory.MemoryManager
import uk.co.dmatthews.khip8.memory.Stack
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.co.dmatthews.khip8.display.Display

@ExtendWith(MockKExtension::class)
class CpuUnitTest {
    @InjectMockKs private lateinit var cpu: Cpu

    @MockK(relaxed = true) private lateinit var memoryManager: MemoryManager
    @MockK(relaxed = true) private lateinit var instructionDecoder: InstructionDecoder
    @MockK(relaxed = true) private lateinit var display: Display

    @Test
    fun `ret sets correct value to pc`() {
        val stackValue = 42.toUInt()

        val stack = mockk<Stack>()
        every { stack.pop() } returns stackValue
        every { memoryManager.stack } returns stack

        cpu.doReturn(UNUSED_VALUE)

        // Value popped from the stack set to the PC
        verify { stack.pop() }
        verify { memoryManager setProperty MemoryManager::PC.name value stackValue }
    }

    @Test
    fun `jmp sets correct value to pc`() {
        cpu.jump(0x1321.toUInt())

        verify { memoryManager setProperty MemoryManager::PC.name value 0x321.toUInt() }
    }

    @Test
    fun `Clear screen calls the display to clear`() {
        cpu.clearScreen(UNUSED_VALUE)

        verify { display.clear() }
    }

    @Test
    fun `call puts pc on stack then sets pc to nnn`() {
        val pcValue = 42.toUInt()

        val stack = mockk<Stack>()
        every { stack.push(pcValue) } just runs
        every { memoryManager.stack } returns stack
        every { memoryManager getProperty MemoryManager::PC.name } returns pcValue

        cpu.call(0x2321.toUInt())

        verify { stack.push(pcValue) }
        verify { memoryManager setProperty MemoryManager::PC.name value 0x321.toUInt() }
    }

    companion object {
        private val UNUSED_VALUE : UInt = 1.toUInt()
    }
}