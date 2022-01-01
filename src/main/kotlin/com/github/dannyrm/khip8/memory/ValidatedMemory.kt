package com.github.dannyrm.khip8.memory

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

    fun clear() {
        for (i in memory.indices) {
            memory[i] = 0u
        }
    }

    private fun checkRange(address: Int, maxValue: Int) {
        if (address !in 0 until maxValue) {
            throw IllegalArgumentException("Invalid address specified ($address). Must be in range (0..$maxValue)")
        }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        val bytesPerLine = 20
        val emptyChunk = "     "

        memory.chunked(bytesPerLine).forEachIndexed { chunkNumber, chunk ->
            // Prefix containing memory start position followed by the memory in hex
            stringBuilder.append("\t${wordHex(chunkNumber * bytesPerLine)} |")

            chunk.forEach { stringBuilder.append(" ${toHex(it)}") }

            val containsNonZeroData = chunk.any { it != 0.toUByte() }

            if (containsNonZeroData) {
                // Fill in any unused slots in the chunk so that the * always appears at the end of the line
                val numberOfMissingChunks = bytesPerLine - chunk.size

                stringBuilder.append(emptyChunk.repeat(numberOfMissingChunks), "  *")
            }

            stringBuilder.append(System.lineSeparator())
        }

        return stringBuilder.toString()
    }
}