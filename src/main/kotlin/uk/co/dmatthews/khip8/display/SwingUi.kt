package uk.co.dmatthews.khip8.display

import uk.co.dmatthews.khip8.input.KeyboardManager
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferStrategy
import kotlin.math.floor

class SwingUi(private val canvas: Canvas, keyboardManager: KeyboardManager,
              private var onCloseSignal: () -> Unit = {}): Ui, Frame() {
    internal val windowSize: Dimension

    init {
        title = "Khip 8"

        // Setup Graphics
        ignoreRepaint = true
        isResizable = false

        windowSize = getAdjustedDimension(512, 256)

        canvas.size = windowSize
        size = windowSize

        // Halt the Khip8 system and dispose of the window. The program is exited after it has been fully halted.
        val windowListener = object: WindowAdapter() {
            override fun windowClosing(event: WindowEvent) {
                super.windowClosing(event)
                halt()
            }
        }

        this.addWindowListener(windowListener)

        this.addKeyListener(keyboardManager)
        canvas.addKeyListener(keyboardManager)

        this.add(canvas)

        pack()
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
        val frameWidth = windowSize.width.toDouble()
        val frameHeight = windowSize.height.toDouble()

        val adjustedXPixelSize = floor(frameWidth / displayMemoryWidth).toInt()
        val adjustedYPixelSize = floor(frameHeight / displayMemoryHeight).toInt()

        return intArrayOf(adjustedXPixelSize, adjustedYPixelSize)
    }

    /**
     * Clear the canvas and return the Graphics context.
     */
    private fun startNewFrame(): Graphics2D {
        val graphics = obtainBufferStrategy().drawGraphics as Graphics2D
        graphics.clearRect(0, 0, windowSize.width, windowSize.height)
        return graphics
    }

    private fun obtainBufferStrategy(): BufferStrategy {
        if (canvas.bufferStrategy == null) {
            canvas.createBufferStrategy(2)
        }
        return canvas.bufferStrategy
    }

    private fun getAdjustedDimension(width: Int, height: Int): Dimension {
        val screenInsets = Toolkit.getDefaultToolkit().getScreenInsets(graphicsConfiguration)

        val widthInsetValue = screenInsets.left + screenInsets.right
        val heightInsetValue = screenInsets.top + screenInsets.bottom

        return Dimension(width+widthInsetValue, height+heightInsetValue)
    }
}