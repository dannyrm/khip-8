package uk.co.dmatthews.khip8.display

import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import uk.co.dmatthews.khip8.input.KeyboardManager
import java.awt.Canvas
import java.awt.Color
import java.awt.Graphics2D
import java.awt.image.BufferStrategy
import kotlin.test.AfterTest
import kotlin.test.BeforeTest

class SwingUiUnitTest {
    private lateinit var swingUi: SwingUi
    private lateinit var canvas: Canvas
    private lateinit var keyboardManager: KeyboardManager

    @AfterTest
    fun afterTest() {
        swingUi.dispose()
    }

    @BeforeTest
    fun beforeTest() {
        canvas = mockk(relaxed = true)
        keyboardManager = mockk(relaxed = true)

        fun initFunction(): Unit = println()

        swingUi = SwingUi(canvas, keyboardManager)
        swingUi.init(::initFunction)
    }

    @Test
    fun `title is correct`() {
        expectThat(swingUi.title).isEqualTo("Khip 8")
    }

    @Test
    fun `width and height are correct`() {
        expectThat(swingUi.width).isEqualTo(1024)
        expectThat(swingUi.height).isEqualTo(768)
    }

    @Test
    fun `Panel ignores repaint requests`() {
        expectThat(swingUi.ignoreRepaint).isEqualTo(true)
    }

    @Test
    fun `Canvas added`() {
        expectThat(swingUi.components.size).isEqualTo(1)
        expectThat(swingUi.components[0]).isEqualTo(canvas)
    }

    @Test
    fun `Has window listener`() {
        expectThat(swingUi.windowListeners.size).isEqualTo(1)
    }

    @Test
    fun `canvas and jframe both Have key listener`() {
        verify { canvas.addKeyListener(keyboardManager) }

        expectThat(swingUi.keyListeners.size).isEqualTo(1)
    }

    @Test
    fun `Halting calls onCloseSignal function`() {
        val initFunction: () -> Unit = {
            swingUi.ignoreRepaint = false
        }

        swingUi.init(initFunction)
        swingUi.halt()

        // initFunction is called which sets ignoreRepaint to false.
        expectThat(swingUi.ignoreRepaint).isFalse()
    }

    @Test
    fun `init makes window visible`() {
        expectThat(swingUi.isVisible).isTrue()
    }

    @Test
    fun `Update empty display`() {
        val displayMemory = DisplayMemory()

        val bufferStrategy = mockk<BufferStrategy>(relaxed = true)
        val graphics2D = mockk<Graphics2D>(relaxed = true)

        every { canvas.bufferStrategy } returns bufferStrategy
        every { bufferStrategy.drawGraphics } returns graphics2D

        swingUi.update(displayMemory)

        verify { graphics2D.color = Color.BLACK }

        // Cleans the canvas before drawing
        verify { graphics2D.clearRect(0, 0, 1024, 768) }

        // No pixels populated so shouldn't draw anything
        verify(inverse = true) { graphics2D.drawRect(any(), any(), any(), any()) }

        // Paints to the canvas after populating it
        verify { bufferStrategy.show() }
    }

    @Test
    fun `Update does nothing if frame is not visible`() {
        val displayMemory = DisplayMemory()

        val bufferStrategy = mockk<BufferStrategy>(relaxed = true)
        val graphics2D = mockk<Graphics2D>(relaxed = true)

        every { canvas.bufferStrategy } returns bufferStrategy
        every { bufferStrategy.drawGraphics } returns graphics2D

        swingUi.isVisible = false
        swingUi.update(displayMemory)

        verify(inverse = true) { graphics2D.color = Color.BLACK }
        verify(inverse = true) { graphics2D.clearRect(0, 0, 1024, 768) }
        verify(inverse = true) { bufferStrategy.show() }
    }

    @Test
    fun `Update populated display`() {
        val displayMemory = DisplayMemory()
        displayMemory[10, 10] = 0xFFu

        val bufferStrategy = mockk<BufferStrategy>(relaxed = true)
        val graphics2D = mockk<Graphics2D>(relaxed = true)

        every { canvas.bufferStrategy } returns bufferStrategy
        every { bufferStrategy.drawGraphics } returns graphics2D

        swingUi.update(displayMemory)

        verify { graphics2D.color = Color.BLACK }

        // Cleans the canvas before drawing
        verify { graphics2D.clearRect(0, 0, 1024, 768) }

        // Draws all of the populated pixels
        verify { graphics2D.fillRect(160, 240, 16, 24) }
        verify { graphics2D.fillRect(176, 240, 16, 24) }
        verify { graphics2D.fillRect(192, 240, 16, 24) }
        verify { graphics2D.fillRect(208, 240, 16, 24) }
        verify { graphics2D.fillRect(224, 240, 16, 24) }
        verify { graphics2D.fillRect(240, 240, 16, 24) }
        verify { graphics2D.fillRect(256, 240, 16, 24) }
        verify { graphics2D.fillRect(272, 240, 16, 24) }

        // Paints to the canvas after populating it
        verify { bufferStrategy.show() }
    }

    @Test
    fun `Calculate Pixel size`() {
        val displayMemory = mockk<DisplayMemory>()
        every { displayMemory.dimensions() } returns intArrayOf(64, 32)

        expectThat(swingUi.calculatePixelSize(displayMemory)).isEqualTo(intArrayOf(16, 24))
    }
}