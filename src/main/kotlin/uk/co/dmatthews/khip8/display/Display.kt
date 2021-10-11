package uk.co.dmatthews.khip8.display

import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Display(private val displayMemory: DisplayMemory,
              private val ui: Ui, private var halt: Boolean = false) {

    operator fun set(x: Int, y: Int, value: UByte) {
        displayMemory[x,y] = value
    }

    suspend fun start(onCloseSignal: () -> Unit) {
        ui.init(onCloseSignal)
        update()
    }

    fun halt() {
        LOG.info("Halting display...")
        halt = true
    }

    suspend fun update() {
        while (!halt) {
            ui.update(displayMemory)
            delay(FREQUENCY_IN_MILLIS)
        }
    }

    fun clear() {
        displayMemory.clear()
    }

    override fun toString(): String {
        return "Display Memory {${System.lineSeparator()}$displayMemory}"
    }

    companion object {
        // 60 Hz, calculated as 1000 / 60 = 16.66666666666667 rounded up to 17. This is slightly inaccurate, clocking
        // in at 1020 millis every 60 ticks
        const val FREQUENCY_IN_MILLIS = 17L
        private val LOG: Logger = LoggerFactory.getLogger(Display::class.java)
    }
}