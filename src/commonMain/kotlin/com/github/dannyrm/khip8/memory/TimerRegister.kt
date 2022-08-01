package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.RunningState
import com.github.dannyrm.khip8.RunningState.RUNNING
import com.github.dannyrm.khip8.observers.SystemStateObserver
import com.github.dannyrm.khip8.logger
import org.koin.core.annotation.Single

@Single
open class TimerRegister(open var value: UByte = 0u): SystemStateObserver {

    open suspend fun tick() {
        if (value > 0u && state == RUNNING) {
            LOG.debug { "Decrementing timer register. Current value: $value" }
            value--
        }
    }

    open var state: RunningState = RUNNING
        set(value) {
            field = value

            if (value == RunningState.STOPPED) {
                this.value = 0u
            }
        }

    companion object {
        private val LOG = logger(this::class)
    }

    override fun receiveEvent(runningState: RunningState) {
        state = runningState
    }
}
