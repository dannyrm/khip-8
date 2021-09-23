package memory

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
}