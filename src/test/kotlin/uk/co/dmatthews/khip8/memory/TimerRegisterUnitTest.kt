package uk.co.dmatthews.khip8.memory

import kotlinx.coroutines.*
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runBlockingTest
import kotlinx.coroutines.test.setMain
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import uk.co.dmatthews.khip8.CoroutineTest
import kotlin.test.AfterTest
import kotlin.test.BeforeTest
import kotlin.test.Test

class TimerRegisterUnitTest: CoroutineTest() {
    private lateinit var timerRegister: TimerRegister
    private var expectedTimerValue: UByte = 0u

    @Test
    fun `Check decrements correctly and halts correctly`() {
        expectedTimerValue = 5u

        timerRegister = TimerRegister()
        timerRegister.value = expectedTimerValue

        runBlockingTest {
            launch { timerRegister.start() }
            val checkDecrementJob = launch { checkDecrement() }
            launch { haltTimerRegister(checkDecrementJob) }
        }
    }

    @Test
    fun `Clears timer value`() {
        timerRegister = TimerRegister()
        timerRegister.value = 54u

        timerRegister.clear()

        expectThat(timerRegister.value).isEqualTo(0u)
    }

    suspend fun checkDecrement() {
        while (expectedTimerValue >= 0u) {
            delay(16)
            expectThat(timerRegister.value).isEqualTo(expectedTimerValue)
            if (expectedTimerValue > 0u) {
                expectedTimerValue--
            }
        }
    }

    suspend fun haltTimerRegister(checkDecrementJob: Job) {
        delay(2000)
        timerRegister.halt()
        checkDecrementJob.cancelAndJoin()
    }
}