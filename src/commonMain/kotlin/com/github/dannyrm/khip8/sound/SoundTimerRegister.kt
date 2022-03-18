package com.github.dannyrm.khip8.sound

import com.github.dannyrm.khip8.Khip8State
import com.github.dannyrm.khip8.Khip8State.*
import com.github.dannyrm.khip8.memory.TimerRegister

class SoundTimerRegister(private val soundGenerator: SoundGenerator): TimerRegister() {

    override suspend fun tick() {
        super.tick()

        when (super.state) {
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