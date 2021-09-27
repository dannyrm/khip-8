package uk.co.dmatthews.khip8

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

/**
 * Before and after code taken from https://kotlin.github.io/kotlinx.coroutines/kotlinx-coroutines-test/index.html
 */
open class CoroutineTest {
    private val mainThreadSurrogate = newSingleThreadContext("thread")

    @BeforeTest
    fun setUp() {
        Dispatchers.setMain(mainThreadSurrogate)
    }

    @AfterTest
    fun tearDown() {
        Dispatchers.resetMain() // reset main dispatcher to the original Main dispatcher
        mainThreadSurrogate.close()
    }
}