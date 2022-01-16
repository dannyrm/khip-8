package com.github.dannyrm.khip8.display.model

import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.test.utils.BaseTest
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlin.test.Test

class DisplayUnitTest: BaseTest() {
    @MockK(relaxed = true) private lateinit var displayMemory: DisplayMemory
    @MockK(relaxed = true) private lateinit var ui: Ui

    @InjectMockKs
    private lateinit var display: Display

    @Test
    fun `Clearing the display clears the display memory`() {
        display.clear()

        verify { displayMemory.clear() }
    }

    @Test
    fun `Tick works correctly`() {
        display.tick()

        verify { ui.update(displayMemory) }
    }
}