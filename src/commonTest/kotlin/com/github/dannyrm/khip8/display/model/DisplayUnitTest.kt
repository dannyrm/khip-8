package com.github.dannyrm.khip8.display.model

import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.test.utils.BaseTest
import io.mockk.every
import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.verify
import kotlin.test.Test
import kotlin.test.expect

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
    fun `Setting a pixel sets the display memory`() {
        display[0,0] = 1u

        verify { displayMemory[0,0] = 1u }
    }

    @Test
    fun `has collision returns true if there is a collision then resets the flag`() {
        every { displayMemory.collision } returns true

        expect(true) { display.hasCollision() }

        verify { displayMemory.collision = false }
    }

    @Test
    fun `has collision returns false if there is not a collision`() {
        every { displayMemory.collision } returns false

        expect(false) { display.hasCollision() }

        verify(inverse = true) { displayMemory.collision = any() }
    }
}