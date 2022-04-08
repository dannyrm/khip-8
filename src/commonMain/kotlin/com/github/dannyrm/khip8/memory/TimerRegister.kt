package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.Khip8State
import com.github.dannyrm.khip8.Khip8State.RUNNING
import com.github.dannyrm.khip8.logger

open class TimerRegister(open var value: UByte = 0u) {

    open suspend fun tick() {
        if (value > 0u && state == RUNNING) {
            LOG.debug { "Decrementing timer register. Current value: $value" }
            value--
        }
    }

    open var state: Khip8State = RUNNING
        set(value) {
            field = value

            if (value == Khip8State.STOPPED) {
                this.value = 0u
            }
        }

    companion object {
        private val LOG = logger(this::class)
    }
}
