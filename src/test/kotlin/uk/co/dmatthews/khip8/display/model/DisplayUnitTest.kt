package uk.co.dmatthews.khip8.display.model

import io.mockk.impl.annotations.InjectMockKs
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import uk.co.dmatthews.khip8.display.model.Display
import uk.co.dmatthews.khip8.display.model.DisplayMemory
import uk.co.dmatthews.khip8.display.view.Ui

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

    @Test
    fun `Tick works correctly`() {
        display.tick()

        verify { ui.update(displayMemory) }
    }

    @Test
    fun `Init works correctly`() {
        val myFunction = {}
        display.init(myFunction)

        verify { ui.init(myFunction) }
    }
}