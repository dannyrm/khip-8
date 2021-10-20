package uk.co.dmatthews.khip8.memory

import strikt.api.expectThat
import strikt.assertions.isEqualTo
import kotlin.test.Test

class TimerRegisterUnitTest {
    private lateinit var timerRegister: TimerRegister
    private var expectedTimerValue: UByte = 0u

    @Test
    fun `Check decrements correctly`() {
        expectedTimerValue = 5u

        timerRegister = TimerRegister()
        timerRegister.value = expectedTimerValue

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
        timerRegister = TimerRegister()
        timerRegister.value = 54u

        timerRegister.clear()

        expectThat(timerRegister.value).isEqualTo(0u)
    }
}