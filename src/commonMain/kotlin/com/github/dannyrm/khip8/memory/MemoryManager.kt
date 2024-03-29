package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.config.MemoryConfig
import com.github.dannyrm.khip8.lineSeparator
import createBigEndianWordFromBytes
import wordHex

@OptIn(ExperimentalUnsignedTypes::class)
class MemoryManager(private val memoryConfig: MemoryConfig,
                    internal val stack: Stack = Stack(memoryConfig.stackSize),
                    internal val ram: ValidatedMemory = ValidatedMemory(memoryConfig.memorySize),
                    internal val registers: ValidatedMemory = ValidatedMemory(memoryConfig.numberOfGeneralPurposeRegisters)
) {
    var i: UInt = 0u // 16-bits, generally stores memory addresses so only lowest 12 bits usually used

    var pc: UInt = memoryConfig.programStartAddress.toUInt() // 16 bits, program counter
        set(value) { field = value % 0x10000u }

    fun loadProgram(input: ByteArray?): Boolean {
        return input?.run {
            input.toUByteArray().forEachIndexed { index, byte ->
                ram[memoryConfig.programStartAddress + index] = byte
            }
            true
        } ?: false
    }

    fun fetchNextInstruction(): UInt {
        val pc = pc.toInt()
        val instruction = createBigEndianWordFromBytes(ram[pc], ram[pc + 1])
        this.pc += 2u

        return instruction
    }

    fun resetMemory() {
        i = 0u

        stack.clear()
        ram.clear()
        registers.clear()

        pc = memoryConfig.programStartAddress.toUInt()

        loadSpriteDigitsIntoMemory()
    }

    /**
     * Instructions are 16 bits long
     */
    fun skipNextInstruction() {
        pc += 2u
    }

    fun loadSpriteDigitsIntoMemory() {
        populateRam(
            memoryConfig.interpreterStartAddress.toUInt(),
            arrayOf(
                ubyteArrayOf(0xF0u, 0x90u, 0x90u, 0x90u, 0xF0u), // Zero
                ubyteArrayOf(0x20u, 0x60u, 0x20u, 0x20u, 0x70u), // One
                ubyteArrayOf(0xF0u, 0x10u, 0xF0u, 0x80u, 0xF0u), // Two
                ubyteArrayOf(0xF0u, 0x10u, 0xF0u, 0x10u, 0xF0u), // Three
                ubyteArrayOf(0x90u, 0x90u, 0xF0u, 0x10u, 0x10u), // Four
                ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x10u, 0xF0u), // Five
                ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x90u, 0xF0u), // Six
                ubyteArrayOf(0xF0u, 0x10u, 0x20u, 0x40u, 0x40u), // Seven
                ubyteArrayOf(0xF0u, 0x90u, 0xF0u, 0x90u, 0xF0u), // Eight
                ubyteArrayOf(0xF0u, 0x90u, 0xF0u, 0x10u, 0xF0u), // Nine
                ubyteArrayOf(0xF0u, 0x90u, 0xF0u, 0x90u, 0x90u), // A
                ubyteArrayOf(0xE0u, 0x90u, 0xE0u, 0x90u, 0xE0u), // B
                ubyteArrayOf(0xF0u, 0x80u, 0x80u, 0x80u, 0xF0u), // C
                ubyteArrayOf(0xE0u, 0x90u, 0x90u, 0x90u, 0xE0u), // D
                ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x80u, 0xF0u), // E
                ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x80u, 0x80u) // F
            )
        )
    }

    fun getLocationOfSpriteDigit(digit: UInt): UInt {
        return memoryConfig.interpreterStartAddress.toUInt() + (digit * NUM_BYTES_PER_DIGIT)
    }

    private fun populateRam(startLocation: UInt, arrays: Array<UByteArray>) {
        var currentLocation = startLocation.toInt()

        arrays.forEach { array ->
            array.forEach { element -> ram[currentLocation++] = element }
        }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        val newLine = lineSeparator()

        stringBuilder.append(
            "Registers: {$newLine" +
            "\tI = ${wordHex(i)}, PC = ${wordHex(pc)}$newLine" +
            "}$newLine" +
            "General Registers: {$newLine" +
            registers +
            "}$newLine" +
            "Stack = {$newLine" +
            stack +
            "}$newLine" +
            "Ram = {$newLine" +
            ram +
            "}$newLine"
        )

        return stringBuilder.toString()
    }

    companion object {
        private const val NUM_BYTES_PER_DIGIT = 5u
    }
}