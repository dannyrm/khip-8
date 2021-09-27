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
    private var expectedTimerValue: UByte = 0.toUByte()

    @Test
    fun `Check decrements correctly and halts correctly`() {
        expectedTimerValue = 5.toUByte()

        timerRegister = TimerRegister()
        timerRegister.value = expectedTimerValue

        runBlockingTest {
            launch { timerRegister.start() }
            val checkDecrementJob = launch { checkDecrement() }
            launch { haltTimerRegister(checkDecrementJob) }
        }
    }

    suspend fun checkDecrement() {
        while (expectedTimerValue >= 0.toUByte()) {
            delay(16)
            expectThat(timerRegister.value).isEqualTo(expectedTimerValue)
            if (expectedTimerValue > 0.toUByte()) {
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