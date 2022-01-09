package com.github.dannyrm.khip8.sound

import com.github.dannyrm.khip8.config.SoundConfig

expect class SoundGenerator(soundConfig: SoundConfig) {
    fun start()
    fun stop()
}