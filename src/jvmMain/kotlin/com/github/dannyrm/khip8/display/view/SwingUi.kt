package com.github.dannyrm.khip8.display.view

import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.input.KeyboardManager
import com.github.dannyrm.khip8.util.calculatePixelSize
import com.github.dannyrm.khip8.util.logger
import kotlinx.coroutines.*
import java.awt.*
import java.awt.event.WindowAdapter
import java.awt.event.WindowEvent
import java.awt.image.BufferStrategy

class SwingUi(private val displayMemory: DisplayMemory,
              keyboardManager: KeyboardManager,
              config: Config): Ui, Frame() {
    private val pixelSizes: Pair<Int, Int>
    private val canvas: Canvas

    init {
        title = "Khip 8"

        // We're going to deal with painting and refreshing the frame manually, so we can control the refresh rate
        ignoreRepaint = true
        isResizable = false

        val windowSize = getAdjustedDimension(config.frontEndConfig.windowWidth, config.frontEndConfig.windowHeight)
        size = windowSize

        // Setup canvas which all graphics are painted onto
        canvas = Canvas()
        canvas.size = windowSize
        canvas.addKeyListener(keyboardManager)
        this.add(canvas)

        this.addKeyListener(keyboardManager)

        pixelSizes = calculatePixelSize(displayMemory, size.width, size.height)

        pack()
    }

    override suspend fun start(config: Config, parentJob: Job) {
        isVisible = true

        addWindowListener(
            object: WindowAdapter() {
                override fun windowClosing(event: WindowEvent) {
                    super.windowClosing(event)

                    parentJob.cancelChildren()

                    dispose()
                }
            }
        )

        val delayBetweenFrames = 1000L / config.systemSpeedConfig.displayRefreshRate.toLong()

        while (true) {
            update()
            delay(delayBetweenFrames)
        }
    }

    private fun update() {
        val graphics: Graphics2D? =
            try {
                obtainBufferStrategy().drawGraphics as Graphics2D
            } catch (e: java.lang.IllegalStateException) {
                null
            }

        // Skips rendering if the graphics context can't be obtained.
        if (graphics != null) {
            graphics.clearRect(0, 0, width, height)

            graphics.color = Color.BLACK

            val xPixelSize = pixelSizes.first
            val yPixelSize = pixelSizes.second

            for (x in 0 until displayMemory.dimensions()[0]) {
                for (y in 0 until displayMemory.dimensions()[1]) {
                    if (displayMemory[x, y]) {
                        graphics.fillRect(x*xPixelSize, y*yPixelSize, xPixelSize, yPixelSize)
                    }
                }
            }

            render()
        } else {
            LOG.warn { "Unable to obtain graphics context. Skipping rendering this frame" }
        }
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

        return Dimension(width + widthInsetValue, height + heightInsetValue)
    }

    companion object {
        private val LOG = logger(this::class)
    }
}