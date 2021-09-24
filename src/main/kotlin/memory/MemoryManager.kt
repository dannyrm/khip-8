package memory

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

    override fun toString(): String {
        val stringBuilder = StringBuilder()

        val nl = System.lineSeparator()

        stringBuilder.append(
            "Registers: {" +
            nl +
            "\tI = ${wordHex(I)}, PC = ${wordHex(PC)}, DT = ${toHex(delayRegister)}, ST = ${toHex(soundRegister)}" +
            nl +
            "}" +
            nl +
            "General Registers: {" +
            nl +
            registers +
            "}" +
            nl +
            "Stack = {" +
            nl +
            stack +
            "}" +
            nl +
            "Ram = {" +
            nl +
            ram +
            "}" +
            nl
        )

        return stringBuilder.toString()
    }

    companion object {
        private const val MEMORY_SIZE = 4096 // 4 KB memory available to Chip 8
        private const val NUM_GENERAL_PURPOSE_REGISTERS = 16 // 16 registers, named Vx where x = 1...F
        private const val STACK_SIZE = 16 // Up to 16 levels of nested subroutines

        private const val PROGRAM_START_ADDRESS = 0x200
    }
}