package com.github.dannyrm.khip8.sound

import com.github.dannyrm.khip8.Khip8State
import com.github.dannyrm.khip8.Khip8State.*
import com.github.dannyrm.khip8.memory.TimerRegister

class SoundTimerRegister(private val soundGenerator: SoundGenerator): TimerRegister() {

    override var state: Khip8State = RUNNING
        set(value) {
            field = value

            if (state == STOPPED) {
                this.value = 0u
            }
        }

    override suspend fun tick() {
        super.tick()

        when (state) {
            PAUSED -> soundGenerator.stop()
            RUNNING -> {
                if (value > 0u) {
                    soundGenerator.start()
                }
            }
            STOPPED -> soundGenerator.stop()
        }

        if (value <= 0u) {
            soundGenerator.stop()
        }
    }
}