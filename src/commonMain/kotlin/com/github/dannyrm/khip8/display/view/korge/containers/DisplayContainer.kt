package com.github.dannyrm.khip8.display.view.korge.containers

import com.github.dannyrm.khip8.config.ConfigManager
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.soywiz.klock.Frequency
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.graphics
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.vector.rect
import kotlin.math.floor

fun Container.khip8DisplayContainer(displayMemory: DisplayMemory, configManager: ConfigManager): KorgeDisplayContainer = KorgeDisplayContainer(displayMemory, configManager).addTo(this)

class KorgeDisplayContainer(displayMemory: DisplayMemory,
                            configManager: ConfigManager): Container() {

    init {
        val windowWidth = configManager.frontEndConfig().windowWidth
        val windowHeight = configManager.frontEndConfig().windowHeight

        val (xPixelSize, yPixelSize) = calculatePixelSize(displayMemory.dimensions(), windowWidth, windowHeight)

        val displayMemoryWidth = displayMemory.dimensions()[0]
        val displayMemoryHeight = displayMemory.dimensions()[1]

        val graphics = graphics()

        addFixedUpdater(Frequency(configManager.speedConfig().displayRefreshRate.toDouble())) {
            graphics.clear()

            for (x in 0 until displayMemoryWidth) {
                for (y in 0 until displayMemoryHeight) {
                    val pixelColour = if (displayMemory[x, y]) Colors.BLACK else Colors.WHITE

                    graphics.fill(pixelColour) {
                        rect(x * xPixelSize, y * yPixelSize, xPixelSize, yPixelSize)
                    }
                }
            }
        }
    }

    private fun calculatePixelSize(dimensions: IntArray, windowWidth: Int, windowHeight: Int): Pair<Int, Int> {
        val (displayMemoryWidth, displayMemoryHeight) = dimensions

        val adjustedXPixelSize = floor(windowWidth.toDouble()/ displayMemoryWidth).toInt()
        val adjustedYPixelSize = floor(windowHeight.toDouble() / displayMemoryHeight).toInt()

        return Pair(adjustedXPixelSize, adjustedYPixelSize)
    }
}