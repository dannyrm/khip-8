package uk.co.dmatthews.khip8.memory

import kotlinx.coroutines.delay
import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TimerRegister(var value: UByte = 0u, private var halt: Boolean = false) {

    fun halt() {
        halt = true
    }

    suspend fun start() {
        while (!halt) {
            delay(FREQUENCY_IN_MILLIS)
            if (value > ZERO) {
                LOG.info("Decrementing timer register. Current value: $value")
                value--
            }
        }
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(TimerRegister::class.java)
        // 60 Hz, calculated as 1000 / 60 = 16.66666666666667 rounded up to 17. This is slightly inaccurate, clocking
        // in at 1020 millis every 60 ticks
        const val FREQUENCY_IN_MILLIS = 17L
        private val ZERO = 0u
    }
}