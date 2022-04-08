package com.github.dannyrm.khip8.memory

import com.github.dannyrm.khip8.Khip8State
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.expect

class TimerRegisterUnitTest {
    @Test
    fun `Check decrements correctly`() {
        val expectedTimerValue = 5u

        val timerRegister = TimerRegister()
        timerRegister.value = expectedTimerValue.toUByte()

        runTest {
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
    }

    @Test
    fun `Check doesn't decrement if the timer is paused`() {
        val expectedTimerValue = 5u

        val timerRegister = TimerRegister()
        timerRegister.value = expectedTimerValue.toUByte()

        runTest {
            expect(5u) { timerRegister.value }
            timerRegister.tick()
            expect(4u) { timerRegister.value }
            timerRegister.tick()
            expect(3u) { timerRegister.value }
            timerRegister.state = Khip8State.PAUSED

            timerRegister.tick()
            expect(3u) { timerRegister.value }
            timerRegister.tick()
            expect(3u) { timerRegister.value }

            timerRegister.state = Khip8State.RUNNING
            timerRegister.tick()
            expect(2u) { timerRegister.value }
            timerRegister.tick()
            expect(1u) { timerRegister.value }
            timerRegister.tick()
            expect(0u) { timerRegister.value }
        }
    }

    @Test
    fun `Stopping the timer clears timer value`() {
        val timerRegister = TimerRegister()
        timerRegister.value = 54u

        timerRegister.state = Khip8State.STOPPED

        expect(0u) { timerRegister.value }
    }
}