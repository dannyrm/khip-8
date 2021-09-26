package uk.co.dmatthews.khip8.display

import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo

class DisplayUnitTest {

    @Test
    fun `check Display clears correctly`() {
        val display = Display()

        for (i in display.buffer.indices) {
            display.buffer[i] = 0xFF.toULong()
        }

        display.clear()

        for (i in display.buffer.indices) {
            expectThat(display.buffer[i]).isEqualTo(0.toULong())
        }
    }
}