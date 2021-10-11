package uk.co.dmatthews.khip8

import uk.co.dmatthews.khip8.display.Display
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.dmatthews.khip8.cpu.Cpu
import uk.co.dmatthews.khip8.memory.MemoryManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.co.dmatthews.khip8.input.SystemActionInputManager
import uk.co.dmatthews.khip8.util.memoryDump
import java.io.File

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager,
            private val display: Display, private val systemActionInputManager: SystemActionInputManager) {
    init {
        memoryManager.loadSpriteDigitsIntoMemory()
        LOG.debug("Loaded sprite digits into memory...")

        systemActionInputManager.memoryDumpFunction = {
            memoryDump(memoryManager.toString() + display.toString())
        }
    }

    fun load(rom: File) {
        memoryManager.loadProgram(rom)
        LOG.debug("Program Loaded...")
    }

    fun start() {
        LOG.debug("System starting state: ${System.lineSeparator()}")
        LOG.debug(memoryManager.toString())
        LOG.debug(display.toString())

        runBlocking {
            launch { cpu.start() }
            launch { memoryManager.delayRegister.start() }
            launch { memoryManager.soundRegister.start() }
            launch { display.start(::halt) }
        }

        LOG.info("All subsystems halted. Exiting process...")
    }

    fun halt() {
        display.halt()
        cpu.halt()
        memoryManager.delayRegister.halt()
        memoryManager.soundRegister.halt()
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(Khip8::class.java)
    }
}
