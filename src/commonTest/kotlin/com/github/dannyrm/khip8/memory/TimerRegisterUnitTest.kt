package com.github.dannyrm.khip8.memory

import kotlin.test.Test
import kotlin.test.expect

class TimerRegisterUnitTest {
    @Test
    fun `Check decrements correctly`() {
        val expectedTimerValue = 5u

        val timerRegister = TimerRegister()
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
        val timerRegister = TimerRegister()
        timerRegister.value = 54u

        timerRegister.clear()

        expect(0u) { timerRegister.value }
    }
}