package com.github.dannyrm.khip8.sound

import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.expect

class SoundTimerRegisterUnitTest {

    @Test
    fun `Check decrements correctly`() {
        val expectedTimerValue = 5u

        val soundGenerator = mockk<SoundGenerator>(relaxed = true)

        val timerRegister = SoundTimerRegister(soundGenerator = soundGenerator)
        timerRegister.value = expectedTimerValue.toUByte()

        expect(5u) { timerRegister.value }
        timerRegister.tick()
        expect(4u) { timerRegister.value }
        timerRegister.tick()
        expect(3u) { timerRegister.value }
        timerRegister.tick()
        expect(2u) { timerRegister.value }
        timerRegister.tick()
        expect(1u) { timerRegister.value }
        timerRegister.tick()
        expect(0u) { timerRegister.value }
        timerRegister.tick()
        expect(0u) { timerRegister.value }
        timerRegister.tick()
        expect(0u) { timerRegister.value }
    }

    @Test
    fun `Clears timer value`() {
        val soundGenerator = mockk<SoundGenerator>(relaxed = true)

        val timerRegister = SoundTimerRegister(soundGenerator = soundGenerator)
        timerRegister.value = 54u

        timerRegister.clear()

        expect(0u) { timerRegister.value }
    }

    @Test
    fun `Check sound generator starts when the timer is set to a value greater than zero`() {
        val soundGenerator = mockk<SoundGenerator>(relaxed = true)

        val timerRegister = SoundTimerRegister(soundGenerator = soundGenerator)
        timerRegister.value = 54u

        verify { soundGenerator.start() }
    }

    @Test
    fun `Check sound generator does not start when the timer is set to zero`() {
        val soundGenerator = mockk<SoundGenerator>(relaxed = true)

        val timerRegister = SoundTimerRegister(soundGenerator = soundGenerator)
        timerRegister.value = 0u

        verify(inverse = true) { soundGenerator.start() }
    }

    @Test
    fun `Check sound generator only stops when timer reaches zero`() {
        val soundGenerator = mockk<SoundGenerator>(relaxed = true)

        val timerRegister = SoundTimerRegister(soundGenerator = soundGenerator)
        timerRegister.value = 3u

        verify(inverse = true) { soundGenerator.stop() }
        timerRegister.tick()

        verify(inverse = true) { soundGenerator.stop() }
        timerRegister.tick()

        verify(inverse = true) { soundGenerator.stop() }
        timerRegister.tick()

        verify { soundGenerator.stop() }
    }
}