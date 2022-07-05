package com.github.dannyrm.khip8.sound

import com.github.dannyrm.khip8.logger
import com.soywiz.klock.TimeSpan
import com.soywiz.korau.sound.AudioData
import com.soywiz.korau.sound.AudioTone
import com.soywiz.korau.sound.PlatformAudioOutput
import com.soywiz.korau.sound.nativeSoundProvider

class SoundTone internal constructor(internal val audioStream: PlatformAudioOutput, internal val audioData: AudioData) {
    private var isRunning: Boolean = false

    suspend fun start() {
        if (!isRunning) {
            audioStream.add(audioData)
            audioStream.start()

            isRunning = true

            LOG.debug { "Started Audio Tone" }
        }
    }

    fun stop() {
        if (isRunning) {
            audioStream.stop()

            isRunning = false

            LOG.debug { "Stopped Audio Tone" }
        }
    }

    companion object {
        private val LOG = logger(this::class)

        suspend operator fun invoke(frequency: Double, toneLength: Double = 10_000.0): SoundTone {
            return SoundTone(nativeSoundProvider.createAudioStream(frequency.toInt()), AudioTone.generate(TimeSpan(toneLength), frequency))
        }
    }
}