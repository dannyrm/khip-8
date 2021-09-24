package memory

import toHex
import wordHex
import java.lang.IllegalArgumentException

@OptIn(ExperimentalUnsignedTypes::class)
class ValidatedMemory(private val memorySize: Int) {
    private val memory: UByteArray = UByteArray(memorySize)

    operator fun set(address: Int, value: UByte) {
        checkRange(address, memorySize)

        memory[address] = value
    }

    operator fun get(address: Int): UByte {
        checkRange(address, memorySize)

        return memory[address]
    }

    private fun checkRange(address: Int, maxValue: Int) {
        if (address !in 0 until maxValue) {
            throw IllegalArgumentException("Invalid address specified ($address). Must be in range (0..$maxValue)")
        }
    }

    override fun toString(): String {
        val bytesPerLine = 20
        val chunkedMemory = memory.chunked(bytesPerLine)

        val stringBuilder = StringBuilder()

        chunkedMemory.forEachIndexed { index, chunk ->
            printMemoryChunk(chunk, stringBuilder, bytesPerLine, index)
        }

        return stringBuilder.toString()
    }

    private fun printMemoryChunk(memory: List<UByte>, stringBuilder: StringBuilder, chunkSize: Int, chunkNumber: Int) {
        stringBuilder.append("\t${wordHex(chunkNumber*chunkSize)} |")

        var containsNonZeroData = false

        for (byte in memory) {
            if (byte != 0.toUByte()) {
                containsNonZeroData = true
            }

            stringBuilder.append(" ${toHex(byte)}")
        }

        if (containsNonZeroData) {
            // Fill in any unused slots in the chunk so that the * always appears at the end of the line
            val numberOfMissingChunks = chunkSize - memory.size
            val emptyChunk = "     "
            for (i in 0 until numberOfMissingChunks) {
                stringBuilder.append(emptyChunk)
            }

            stringBuilder.append("  *")
        }

        stringBuilder.append(System.lineSeparator())
    }
}
