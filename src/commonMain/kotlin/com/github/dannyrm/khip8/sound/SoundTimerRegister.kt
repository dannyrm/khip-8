package com.github.dannyrm.khip8.sound

import com.github.dannyrm.khip8.memory.TimerRegister

class SoundTimerRegister(private val soundGenerator: SoundGenerator): TimerRegister() {

    override var value
        get() = super.value
        set (value) {
            super.value = value

            if (value > 0u) {
                soundGenerator.start()
            }
        }

    override fun tick() {
        super.tick()

        if (value <= 0u) {
            soundGenerator.stop()
        }
    }
}