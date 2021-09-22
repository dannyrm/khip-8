import memory.MemoryManager
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

    private fun loadFile(fileName: String): File {
        return File(
            MemoryManagerUnitTest::class.java.getResource(fileName).toURI()
        )
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