package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.multiplatform.logger

open class TimerRegister(open var value: UByte = 0u) {

    open fun tick() {
        if (value > 0u) {
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