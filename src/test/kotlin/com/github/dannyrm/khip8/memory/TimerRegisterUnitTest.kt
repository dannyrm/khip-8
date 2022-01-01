package com.github.dannyrm.khip8.memory

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class TimerRegisterUnitTest {
    @Test
    fun `Check decrements correctly`() {
        val expectedTimerValue = 5u

        val timerRegister = TimerRegister()
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
        val timerRegister = TimerRegister()
        timerRegister.value = 54u

        timerRegister.clear()

        expectThat(timerRegister.value).isEqualTo(0u)
    }
}