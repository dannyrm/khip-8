package uk.co.dmatthews.khip8.display

import uk.co.dmatthews.khip8.input.KeyboardManager
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferStrategy

class SwingUi(private val canvas: Canvas, keyboardManager: KeyboardManager,
              private var onCloseSignal: () -> Unit = {}): Ui, Frame() {
    init {
        title = "Khip 8"

        size = Dimension(1024, 768)

        // Setup Graphics
        ignoreRepaint = true

        // Halt the Khip8 system and dispose of the window. The program is exited after it has been fully halted.
        val windowListener = object: WindowAdapter() {
            override fun windowClosing(event: WindowEvent) {
                halt()
            }
        }

        this.addWindowListener(windowListener)

        add(canvas)

        this.addKeyListener(keyboardManager)
        canvas.addKeyListener(keyboardManager)
    }

    override fun init(onCloseSignal: () -> Unit) {
        this.onCloseSignal = onCloseSignal
        isVisible = true
    }

    override fun update(displayMemory: DisplayMemory) {
        if (!isVisible) return

        val graphics = startNewFrame()

        graphics.color = Color.BLACK

        val (width, height) = displayMemory.dimensions()
        val (xPixelSize, yPixelSize) = calculatePixelSize(displayMemory)

        for (x in 0 until width) {
            for (y in 0 until height) {
                if (displayMemory.getPixelState(x, y)) {
                    graphics.fillRect(x*xPixelSize, y*yPixelSize, xPixelSize, yPixelSize)
                }
            }
        }

        render()
    }

    override fun halt() {
        onCloseSignal.invoke()
        dispose()
    }

    private fun render() {
        val bufferStrategy: BufferStrategy = obtainBufferStrategy()

        do {
            try {
                bufferStrategy.drawGraphics as Graphics2D
            } finally {
                graphics.dispose()
            }
        } while (bufferStrategy.contentsRestored())

        bufferStrategy.show()
    }

    internal fun calculatePixelSize(displayMemory: DisplayMemory): IntArray {
        val (displayMemoryWidth, displayMemoryHeight) = displayMemory.dimensions()
        val frameWidth = size.width
        val frameHeight = size.height

        return intArrayOf(frameWidth / displayMemoryWidth, frameHeight / displayMemoryHeight)
    }

    /**
     * Clear the canvas and return the Graphics context.
     */
    private fun startNewFrame(): Graphics2D {
        val graphics = obtainBufferStrategy().drawGraphics as Graphics2D
        graphics.clearRect(0, 0, width, height)
        return graphics
    }

    private fun obtainBufferStrategy(): BufferStrategy {
        if (canvas.bufferStrategy == null) {
            canvas.createBufferStrategy(2)
        }
        return canvas.bufferStrategy
    }
}