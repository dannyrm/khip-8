package com.github.dannyrm.khip8.sound

class SoundGenerator(private val soundTone: SoundTone) {

    suspend fun start() {
        soundTone.start()
    }

    fun stop() {
        soundTone.stop()
    }
}
