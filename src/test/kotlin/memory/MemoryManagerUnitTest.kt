package memory

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import java.io.File

class MemoryManagerUnitTest {

    @Test
    fun `Load Program Into Memory`() {
        val memoryManager = MemoryManager()
        memoryManager.loadProgram(
            loadFile("inputs/15-puzzle.ch8")
        )

        val expectedOutput = hexToByteArray("outputs/15-puzzle.hex")

        // Program starts at 0x200 in memory.
        expectedOutput.forEachIndexed { index, byte ->
            expectThat(memoryManager.ram[0x200+index]).isEqualTo(byte)
        }
    }

    @Test
    fun `Fetch next instruction`() {
        val memoryManager = MemoryManager()
        memoryManager.loadProgram(
            loadFile("inputs/15-puzzle.ch8")
        )

        expectThat(memoryManager.PC).isEqualTo(0x200.toUInt())

        var instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x00E0.toUInt())
        expectThat(memoryManager.PC).isEqualTo(0x202.toUInt())

        instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x6C00.toUInt())
        expectThat(memoryManager.PC).isEqualTo(0x204.toUInt())

        instruction = memoryManager.fetchNextInstruction()
        expectThat(instruction).isEqualTo(0x4C00.toUInt())
        expectThat(memoryManager.PC).isEqualTo(0x206.toUInt())
    }

    private fun loadFile(fileName: String): File {
        return File(this.javaClass.classLoader.getResource(fileName).toURI())
    }

    /**
     * Reads in hex values, with each value separated by a space.
     */
    private fun hexToByteArray(fileName: String): UByteArray {
        val outputList: MutableList<String> = mutableListOf()

        val file = loadFile(fileName)
        file.forEachLine {
            outputList.addAll(it.split(" "))
        }

        val outputByteArray = UByteArray(outputList.size)

        outputList.forEachIndexed { index, s ->
            outputByteArray[index] = Integer.parseInt(s, 16) .toUByte()
        }

        return outputByteArray
    }
}