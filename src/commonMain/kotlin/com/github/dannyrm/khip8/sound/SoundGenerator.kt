package com.github.dannyrm.khip8.sound

import com.github.dannyrm.khip8.logger
import com.soywiz.klock.TimeSpan
import com.soywiz.korau.sound.AudioTone
import com.soywiz.korau.sound.PlatformAudioOutput
import com.soywiz.korau.sound.nativeSoundProvider

class SoundGenerator {
    private lateinit var audioStream: PlatformAudioOutput
    private var isRunning = false

    suspend fun start() {
        if (isRunning) return

        audioStream = nativeSoundProvider.createAudioStream(2000)
        audioStream.add(AudioTone.generate(TimeSpan(10000.0), 2000.0))

        audioStream.start()

        isRunning = true

        LOG.debug { "Started Sound Generation" }
    }

    suspend fun stop() {
        if (!isRunning) return

        audioStream.stop()

        isRunning = false

        LOG.debug { "Stopped Sound Generation" }
    }

    companion object {
        private val LOG = logger(this::class)
    }
}
