package uk.co.dmatthews.khip8.sound

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class SoundTimerRegisterUnitTest {

    @Test
    fun `Check decrements correctly`() {
        val expectedTimerValue = 5u

        val soundGenerator = mockk<SoundGenerator>(relaxed = true)

        val timerRegister = SoundTimerRegister(soundGenerator = soundGenerator)
        timerRegister.value = expectedTimerValue.toUByte()

        expectThat(timerRegister.value).isEqualTo(5u)
        timerRegister.tick()
        expectThat(timerRegister.value).isEqualTo(4u)
        timerRegister.tick()
        expectThat(timerRegister.value).isEqualTo(3u)
        timerRegister.tick()
        expectThat(timerRegister.value).isEqualTo(2u)
        timerRegister.tick()
        expectThat(timerRegister.value).isEqualTo(1u)
        timerRegister.tick()
        expectThat(timerRegister.value).isEqualTo(0u)
        timerRegister.tick()
        expectThat(timerRegister.value).isEqualTo(0u)
        timerRegister.tick()
        expectThat(timerRegister.value).isEqualTo(0u)
    }

    @Test
    fun `Clears timer value`() {
        val soundGenerator = mockk<SoundGenerator>(relaxed = true)

        val timerRegister = SoundTimerRegister(soundGenerator = soundGenerator)
        timerRegister.value = 54u

        timerRegister.clear()

        expectThat(timerRegister.value).isEqualTo(0u)
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