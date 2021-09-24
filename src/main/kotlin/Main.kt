import cpu.Cpu
import cpu.InstructionDecoder
import memory.MemoryManager
import java.io.File

fun main(args: Array<String>) {
    val memoryManager = MemoryManager()
    val instructionDecoder = InstructionDecoder()
    val cpu = Cpu(memoryManager, instructionDecoder)
    instructionDecoder.setCpu(cpu)

    val khip8 = Khip8(cpu, memoryManager)
    khip8.load(File(args[0]))
    khip8.start()
}