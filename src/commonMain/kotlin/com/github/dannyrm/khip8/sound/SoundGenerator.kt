package com.github.dannyrm.khip8.sound

import org.koin.core.annotation.Single

@Single
class SoundGenerator(private val soundTone: SoundTone) {

    suspend fun start() {
        soundTone.start()
    }

    fun stop() {
        soundTone.stop()
    }
}
