package uk.co.dmatthews.khip8.memory

import createBigEndianWordFromBytes
import toHex
import wordHex
import java.io.File
import java.io.FileInputStream

@OptIn(ExperimentalUnsignedTypes::class)
class MemoryManager(var delayRegister: TimerRegister = TimerRegister(),
                    var soundRegister: TimerRegister = TimerRegister(),
                    var I: UInt = 0u, // 16-bits, generally stores memory addresses so only lowest 12 bits usually used
                    val stack: Stack = Stack(STACK_SIZE),
                    var PC: UInt = PROGRAM_START_ADDRESS.toUInt(), // 16 bits, program counter
                    val ram: ValidatedMemory = ValidatedMemory(MEMORY_SIZE),
                    val registers: ValidatedMemory = ValidatedMemory(NUM_GENERAL_PURPOSE_REGISTERS),
                    val displayMemory: DisplayMemory = DisplayMemory()
                   ) {

    fun loadProgram(input: File) {
        val inputStream = FileInputStream(input)

        inputStream.use {
            inputStream.readAllBytes().toUByteArray().forEachIndexed { index, byte ->
                ram[PROGRAM_START_ADDRESS + index] = byte
            }
        }
    }

    fun fetchNextInstruction(): UInt {
        val pc = PC.toInt()
        val instruction = createBigEndianWordFromBytes(ram[pc], ram[pc + 1])
        PC += 2u

        return instruction
    }

    /**
     * Instructions are 16 bits long
     */
    fun skipNextInstruction() {
        PC += 2u
    }

    fun loadSpriteDigitsIntoMemory() {
        populateRam(
            INTERPRETER_START_ADDRESS,
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
                ubyteArrayOf(0xF0u, 0x80u, 0xF0u, 0x80u, 0x80u), // F
            )
        )
    }

    private fun populateRam(startLocation: Int, arrays: Array<UByteArray>) {
        var currentLocation = startLocation

        for (array in arrays) {
            for (element in array) {
                ram[currentLocation++] = element
            }
        }
    }

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        val newLine = System.lineSeparator()

        stringBuilder.append(
            "Registers: {$newLine" +
            "\tI = ${wordHex(I)}, PC = ${wordHex(PC)}, DT = ${toHex(delayRegister.value)}, ST = ${toHex(soundRegister.value)}$newLine" +
            "}$newLine" +
            "General Registers: {$newLine" +
            registers +
            "}$newLine" +
            "Stack = {$newLine" +
            stack +
            "}$newLine" +
            "Ram = {$newLine" +
            ram +
            "}$newLine" +
            "Display Memory {$newLine" +
            "$displayMemory" +
            "}"
        )

        return stringBuilder.toString()
    }

    companion object {
        private const val MEMORY_SIZE = 4096 // 4 KB memory available to Chip 8
        private const val NUM_GENERAL_PURPOSE_REGISTERS = 16 // 16 registers, named Vx where x = 1...F
        private const val STACK_SIZE = 16 // Up to 16 levels of nested subroutines

        private const val INTERPRETER_START_ADDRESS = 0x000
        private const val PROGRAM_START_ADDRESS = 0x200
    }
}
