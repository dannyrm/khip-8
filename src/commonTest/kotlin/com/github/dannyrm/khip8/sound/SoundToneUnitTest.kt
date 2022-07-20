package com.github.dannyrm.khip8.sound

import com.soywiz.korau.sound.AudioData
import com.soywiz.korau.sound.PlatformAudioOutput
import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals

class SoundToneUnitTest {

    @Test
    fun `Starting the sound tone starts the audio stream`() {
        val audioStream = mockk<PlatformAudioOutput>(relaxed = true)
        val audioData = mockk<AudioData>()

        val soundTone = SoundTone(audioStream, audioData)

        runTest { soundTone.start() }

        verify { audioStream.start() }
        coVerify { audioStream.add(audioData) }
    }

    @Test
    fun `Stopping the sound tone stops the audio stream`() {
        val audioStream = mockk<PlatformAudioOutput>(relaxed = true)

        val soundTone = SoundTone(audioStream, mockk())

        runTest { soundTone.start() }

        verify { audioStream.start() }

        soundTone.stop()

        verify { audioStream.stop() }
    }

    @Test
    fun `Starting the sound tone twice only starts the audio stream once`() {
        val audioStream = mockk<PlatformAudioOutput>(relaxed = true)

        val soundTone = SoundTone(audioStream, mockk())

        runTest {
            soundTone.start()
            soundTone.start()
        }

        verify(exactly = 1) { audioStream.start() }
    }

    @Test
    fun `Stopping the sound tone twice only stops the audio stream once`() {
        val audioStream = mockk<PlatformAudioOutput>(relaxed = true)

        val soundTone = SoundTone(audioStream, mockk())

        runTest { soundTone.start() }

        verify { audioStream.start() }

        soundTone.stop()
        soundTone.stop()

        verify(exactly = 1) { audioStream.stop() }
    }

    @Test
    fun `Stopping the sound tone does nothing if it hasn't already been started`() {
        val audioStream = mockk<PlatformAudioOutput>(relaxed = true)

        val soundTone = SoundTone(audioStream, mockk())

        soundTone.stop()

        verify(inverse = true) { audioStream.stop() }
    }

    @Test
    fun `Starting and stopping the sound works correctly`() {
        val audioStream = mockk<PlatformAudioOutput>(relaxed = true)

        val soundTone = SoundTone(audioStream, mockk())

        runTest {
            soundTone.start()
            soundTone.stop()
            soundTone.start()
            soundTone.stop()
        }

        verify(exactly = 2) { audioStream.start() }
        verify(exactly = 2) { audioStream.stop() }
    }

    @Test
    fun `Check audio stream creation`() {
        runTest {
            val soundTone = SoundTone(2000.0,10_000.0)

            assertEquals(2000, soundTone.audioStream.frequency)

            // Should be 44100 * 10 samples in length
            assertEquals(44100*10, soundTone.audioData.totalSamples)
        }
    }
}
