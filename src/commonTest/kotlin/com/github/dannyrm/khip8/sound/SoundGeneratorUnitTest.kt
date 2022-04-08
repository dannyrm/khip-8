package com.github.dannyrm.khip8.sound

import io.mockk.coVerify
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.test.runTest
import kotlin.test.Test

class SoundGeneratorUnitTest {

    @Test
    fun `Start starts the sound tone`() {
        val soundTone = mockk<SoundTone>(relaxed = true)

        val soundGenerator = SoundGenerator(soundTone)

        runTest {
            soundGenerator.start()
        }

        coVerify {
            soundTone.start()
        }
    }

    @Test
    fun `Stop stops the sound tone`() {
        val soundTone = mockk<SoundTone>(relaxed = true)

        val soundGenerator = SoundGenerator(soundTone)
        soundGenerator.stop()

        verify {
            soundTone.stop()
        }
    }
}