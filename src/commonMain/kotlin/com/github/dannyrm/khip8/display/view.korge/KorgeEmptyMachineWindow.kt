package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.Khip8
import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.util.calculatePixelSize
import com.soywiz.klock.Frequency
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korge.view.graphics
import com.soywiz.korge.view.position
import com.soywiz.korim.color.Colors
import com.soywiz.korim.text.text
import com.soywiz.korma.geom.vector.rect

class KorgeEmptyMachineWindow(private val displayMemory: DisplayMemory, private val chip8InputManager: Chip8InputManager,
                              private val config: Config, private val khip8: Khip8): Scene() {

    override suspend fun Container.sceneInit() {
        setupRunLoop(this)
    }

    private fun setupRunLoop(container: Container) {
        val windowWidth = config.frontEndConfig.windowWidth
        val windowHeight = config.frontEndConfig.windowHeight

        val (xPixelSize, yPixelSize) = calculatePixelSize(displayMemory, windowWidth, windowHeight)

        val displayMemoryWidth = displayMemory.dimensions()[0]
        val displayMemoryHeight = displayMemory.dimensions()[1]

        val graphics = container.graphics {
            position(0, KorgeEmulatorWindow.TOP_UI_HEIGHT.toInt())
        }

        container.addFixedUpdater(Frequency(config.systemSpeedConfig.displayRefreshRate.toDouble())) {
            graphics.clear()
        }
    }
}