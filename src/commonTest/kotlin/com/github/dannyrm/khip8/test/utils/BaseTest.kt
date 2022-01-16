package com.github.dannyrm.khip8.test.utils

import io.mockk.MockKAnnotations
import kotlin.test.BeforeTest

open class BaseTest {

    @BeforeTest
    fun setup() {
        MockKAnnotations.init(this)
    }
}