package com.github.dannyrm.khip8.sound

import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.memory.TimerRegisterState
import com.github.dannyrm.khip8.memory.TimerRegisterState.PAUSED
import com.github.dannyrm.khip8.memory.TimerRegisterState.RUNNING

class SoundTimerRegister(private val soundGenerator: SoundGenerator): TimerRegister() {

    override var value
        get() = super.value
        set (value) {
            super.value = value

            if (value > 0u && super.state == RUNNING) {
                soundGenerator.start()
            }
        }

    override var state: TimerRegisterState
        get() = super.state
        set(value) {
            super.state = value

            when (super.state) {
                PAUSED -> soundGenerator.stop()
                RUNNING -> {
                    if (super.value > 0u) {
                        soundGenerator.start()
                    }
                }
            }
        }

    override fun tick() {
        super.tick()

        if (value <= 0u) {
            soundGenerator.stop()
        }
    }
}