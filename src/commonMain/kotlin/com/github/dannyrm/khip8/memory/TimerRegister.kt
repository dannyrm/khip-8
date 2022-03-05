package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.memory.TimerRegisterState.RUNNING
import com.github.dannyrm.khip8.util.logger

open class TimerRegister(open var value: UByte = 0u, open var state: TimerRegisterState = RUNNING) {

    open fun tick() {
        if (value > 0u && state == RUNNING) {
            LOG.debug { "Decrementing timer register. Current value: $value" }
            value--
        }
    }

    fun clear() {
        value = 0u
    }

    companion object {
        private val LOG = logger(this::class)
    }
}

enum class TimerRegisterState {
    RUNNING, PAUSED
}