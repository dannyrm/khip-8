package uk.co.dmatthews.khip8.memory

import createBigEndianWordFromBytes
import toHex
import wordHex
import java.io.File
import java.io.FileInputStream

@OptIn(ExperimentalUnsignedTypes::class)
class MemoryManager(var delayRegister: UByte = 0.toUByte(),
                    var soundRegister: UByte = 0.toUByte(),
                    var I: UInt = 0.toUInt(), // 16-bits, generally stores memory addresses so only lowest 12 bits usually used
                    val stack: Stack = Stack(STACK_SIZE),
                    var PC: UInt = PROGRAM_START_ADDRESS.toUInt(), // 16 bits, program counter
                    val ram: ValidatedMemory = ValidatedMemory(MEMORY_SIZE),
                    val registers: ValidatedMemory = ValidatedMemory(NUM_GENERAL_PURPOSE_REGISTERS)
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
        PC += 2.toUInt()

        return instruction
    }

    fun loadSpriteDigitsIntoMemory() {
        populateRam(
            INTERPRETER_START_ADDRESS,
            arrayOf(
                ubyteArrayOf(0xF0.toUByte(), 0x90.toUByte(), 0x90.toUByte(), 0x90.toUByte(), 0xF0.toUByte()), // Zero
                ubyteArrayOf(0x20.toUByte(), 0x60.toUByte(), 0x20.toUByte(), 0x20.toUByte(), 0x70.toUByte()), // One
                ubyteArrayOf(0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte()), // Two
                ubyteArrayOf(0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte()), // Three
                ubyteArrayOf(0x90.toUByte(), 0x90.toUByte(), 0xF0.toUByte(), 0x10.toUByte(), 0x10.toUByte()), // Four
                ubyteArrayOf(0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte()), // Five
                ubyteArrayOf(0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte()), // Six
                ubyteArrayOf(0xF0.toUByte(), 0x10.toUByte(), 0x20.toUByte(), 0x40.toUByte(), 0x40.toUByte()), // Seven
                ubyteArrayOf(0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte()), // Eight
                ubyteArrayOf(0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte(), 0x10.toUByte(), 0xF0.toUByte()), // Nine
                ubyteArrayOf(0xF0.toUByte(), 0x90.toUByte(), 0xF0.toUByte(), 0x90.toUByte(), 0x90.toUByte()), // A
                ubyteArrayOf(0xE0.toUByte(), 0x90.toUByte(), 0xE0.toUByte(), 0x90.toUByte(), 0xE0.toUByte()), // B
                ubyteArrayOf(0xF0.toUByte(), 0x80.toUByte(), 0x80.toUByte(), 0x80.toUByte(), 0xF0.toUByte()), // C
                ubyteArrayOf(0xE0.toUByte(), 0x90.toUByte(), 0x90.toUByte(), 0x90.toUByte(), 0xE0.toUByte()), // D
                ubyteArrayOf(0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte()), // E
                ubyteArrayOf(0xF0.toUByte(), 0x80.toUByte(), 0xF0.toUByte(), 0x80.toUByte(), 0x80.toUByte()), // F
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
            "\tI = ${wordHex(I)}, PC = ${wordHex(PC)}, DT = ${toHex(delayRegister)}, ST = ${toHex(soundRegister)}$newLine" +
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
        private const val MEMORY_SIZE = 4096 // 4 KB memory available to Chip 8
        private const val NUM_GENERAL_PURPOSE_REGISTERS = 16 // 16 registers, named Vx where x = 1...F
        private const val STACK_SIZE = 16 // Up to 16 levels of nested subroutines

        private const val INTERPRETER_START_ADDRESS = 0x000
        private const val PROGRAM_START_ADDRESS = 0x200
    }
}
