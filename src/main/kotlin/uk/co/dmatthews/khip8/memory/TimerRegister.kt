package uk.co.dmatthews.khip8.memory

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class TimerRegister(var value: UByte = 0u) {

    fun tick() {
        if (value > 0u) {
            LOG.debug("Decrementing timer register. Current value: $value")
            value--
        }
    }

    fun clear() {
        value = 0u
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(TimerRegister::class.java)
    }
}