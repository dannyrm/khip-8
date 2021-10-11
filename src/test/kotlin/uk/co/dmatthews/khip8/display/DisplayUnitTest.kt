package uk.co.dmatthews.khip8.display

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class DisplayUnitTest {
    @MockK(relaxed = true) private lateinit var displayMemory: DisplayMemory
    @MockK(relaxed = true) private lateinit var ui: Ui

    @InjectMockKs private lateinit var display: Display

    @Test
    fun `Clearing the display clears the display memory`() {
        display.clear()

        verify { displayMemory.clear() }
    }
}