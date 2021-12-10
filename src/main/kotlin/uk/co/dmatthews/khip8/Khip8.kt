package uk.co.dmatthews.khip8

import uk.co.dmatthews.khip8.display.model.Display
import uk.co.dmatthews.khip8.cpu.Cpu
import uk.co.dmatthews.khip8.memory.MemoryManager
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.co.dmatthews.khip8.util.waitFor
import java.io.File
import java.math.BigDecimal
import java.math.RoundingMode

class Khip8(private val cpu: Cpu, private val memoryManager: MemoryManager,
            private val display: Display,
            private var halt: Boolean = false) {

    init {
        memoryManager.loadSpriteDigitsIntoMemory()
        LOG.info("Loaded sprite digits into memory...")
    }

    fun load(rom: File) {
        memoryManager.loadProgram(rom)
        LOG.info("Program Loaded...")
    }

    fun start() {
        LOG.debug("System starting state: ${System.lineSeparator()}")
        LOG.debug(memoryManager.toString())
        LOG.debug(display.toString())

        display.init(::halt)

        val cpuTicksPerPeripheralTick = numberOfCpuTicksPerPeripheralTick()

        val (delayInMillis, delayInNanos) = delayBetweenCycles()
        val delayInMillisLong = delayInMillis as Long
        val delayInNanosInt = delayInNanos as Int

        while (!halt) {
            execute(cpuTicksPerPeripheralTick, delayInMillisLong, delayInNanosInt)
        }

        LOG.info("Halting machine...")
    }

    internal fun execute(cpuTicksPerPeripheralTick: Int, delayInMillis: Long, delayInNanos: Int) {
        for (i in 0 until cpuTicksPerPeripheralTick) {
            cpu.tick()
            waitFor(delayInMillis, delayInNanos)
        }
        memoryManager.delayRegister.tick()
        memoryManager.soundRegister.tick()
        display.tick()
    }

    fun halt() {
        halt = true
    }

    internal fun numberOfCpuTicksPerPeripheralTick(): Int {
        return CPU_SPEED_IN_HZ / TIMERS_AND_DISPLAY_SPEED_IN_HZ
    }

    internal fun delayBetweenCycles(): List<Number> {
        val cyclesDecimal = BigDecimal(1000.0 / CPU_SPEED_IN_HZ.toDouble())
        val numberOfMillis = cyclesDecimal.setScale(0, RoundingMode.DOWN)

        val fractionalValue = (cyclesDecimal - numberOfMillis)
        val numberOfNanos = fractionalValue.toString().replace("0.", "").toBigInteger().toString().substring(0..5).toInt()

        return listOf(numberOfMillis.toLong(), numberOfNanos)
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(Khip8::class.java)

        const val CPU_SPEED_IN_HZ = 540
        const val TIMERS_AND_DISPLAY_SPEED_IN_HZ = 60
    }
}
