import cpu.Cpu
import cpu.InstructionDecoder
import memory.MemoryManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager) {

    fun load(rom: File) {
        memoryManager.loadProgram(rom)
        LOG.trace("Program Loaded...")
    }

    fun start() {
        cpu.start()
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(Khip8::class.java)
    }
}

fun main(args: Array<String>) {
    val memoryManager = MemoryManager()
    val instructionDecoder = InstructionDecoder()
    val cpu = Cpu(memoryManager, instructionDecoder)
    instructionDecoder.setCpu(cpu)

    val khip8 = Khip8(cpu, memoryManager)
    khip8.load(File(args[0]))
    khip8.start()
}