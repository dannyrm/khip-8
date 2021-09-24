import cpu.Cpu
import memory.MemoryManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import java.io.File

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager) {
    init {
        boot()
    }

    fun load(rom: File) {
        memoryManager.loadProgram(rom)
        LOG.debug("Program Loaded...")
    }

    fun start() {
        cpu.start()
    }

    private fun boot() {
        memoryManager.loadSpriteDigitsIntoMemory()
        LOG.debug("Loaded sprite digits into memory...")
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(Khip8::class.java)
    }
}
