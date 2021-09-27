package uk.co.dmatthews.khip8

import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import uk.co.dmatthews.khip8.cpu.Cpu
import uk.co.dmatthews.khip8.memory.MemoryManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.co.dmatthews.khip8.cpu.InstructionDecoder
import uk.co.dmatthews.khip8.memory.DisplayMemory
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
        LOG.debug("System starting state: ${System.lineSeparator()}")
        LOG.debug(memoryManager.toString())

        runBlocking {
            launch { cpu.start() }
            launch { memoryManager.delayRegister.start() }
            launch { memoryManager.soundRegister.start() }
        }
    }

    fun halt() {
        cpu.halt()
        memoryManager.delayRegister.halt()
        memoryManager.soundRegister.halt()
    }

    private fun boot() {
        memoryManager.loadSpriteDigitsIntoMemory()
        LOG.debug("Loaded sprite digits into memory...")
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
