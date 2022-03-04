package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.input.Chip8Inputs
import com.github.dannyrm.khip8.util.calculatePixelSize
import com.soywiz.klock.Frequency
import com.soywiz.korev.Key
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addFixedUpdater
import com.soywiz.korge.view.graphics
import com.soywiz.korge.view.position
import com.soywiz.korim.color.Colors
import com.soywiz.korma.geom.vector.rect

class KorgeEmulatorWindow(private val displayMemory: DisplayMemory, private val chip8InputManager: Chip8InputManager,
                          private val config: Config): Scene() {

    override suspend fun Container.sceneInit() {
        setupKeymap(this)
        setupUi(this)
        setupRunLoop(this)
    }

    private fun setupUi(container: Container) {
        container.uiHorizontalStack(height = 30.0) {
            uiButton(text = "Load ROM").position(0,0)
        }
    }

    private fun setupRunLoop(container: Container) {
        val windowWidth = config.frontEndConfig.windowWidth
        val windowHeight = config.frontEndConfig.windowHeight

        val (xPixelSize, yPixelSize) = calculatePixelSize(displayMemory, windowWidth, windowHeight)

        val displayMemoryWidth = displayMemory.dimensions()[0]
        val displayMemoryHeight = displayMemory.dimensions()[1]

        val graphics = container.graphics {
            position(0,30)
        }

        container.addFixedUpdater(Frequency(config.systemSpeedConfig.displayRefreshRate.toDouble())) {
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

    private fun setupKeymap(container: Container) {
        container.keys {
            down(Key.KP_0) { chip8InputManager[Chip8Inputs.ONE] = true }
            up(Key.KP_0) { chip8InputManager[Chip8Inputs.ONE] = false }

            down(Key.KP_2) { chip8InputManager[Chip8Inputs.TWO] = true }
            up(Key.KP_2) { chip8InputManager[Chip8Inputs.TWO] = false }

            down(Key.KP_3) { chip8InputManager[Chip8Inputs.THREE] = true }
            up(Key.KP_3) { chip8InputManager[Chip8Inputs.THREE] = false }

            down(Key.KP_4) { chip8InputManager[Chip8Inputs.C] = true }
            up(Key.KP_4) { chip8InputManager[Chip8Inputs.C] = false }

            down(Key.Q) { chip8InputManager[Chip8Inputs.FOUR] = true }
            up(Key.Q) { chip8InputManager[Chip8Inputs.FOUR] = false }

            down(Key.W) { chip8InputManager[Chip8Inputs.FIVE] = true }
            up(Key.W) { chip8InputManager[Chip8Inputs.FIVE] = false }

            down(Key.E) { chip8InputManager[Chip8Inputs.SIX] = true }
            up(Key.E) { chip8InputManager[Chip8Inputs.SIX] = false }

            down(Key.R) { chip8InputManager[Chip8Inputs.D] = true }
            up(Key.R) { chip8InputManager[Chip8Inputs.D] = false }

            down(Key.A) { chip8InputManager[Chip8Inputs.SEVEN] = true }
            up(Key.A) { chip8InputManager[Chip8Inputs.SEVEN] = false }

            down(Key.S) { chip8InputManager[Chip8Inputs.EIGHT] = true }
            up(Key.S) { chip8InputManager[Chip8Inputs.EIGHT] = false }

            down(Key.D) { chip8InputManager[Chip8Inputs.NINE] = true }
            up(Key.D) { chip8InputManager[Chip8Inputs.NINE] = false }

            down(Key.F) { chip8InputManager[Chip8Inputs.E] = true }
            up(Key.F) { chip8InputManager[Chip8Inputs.E] = false }

            down(Key.Z) { chip8InputManager[Chip8Inputs.A] = true }
            up(Key.Z) { chip8InputManager[Chip8Inputs.A] = false }

            down(Key.X) { chip8InputManager[Chip8Inputs.ZERO] = true }
            up(Key.X) { chip8InputManager[Chip8Inputs.ZERO] = false }

            down(Key.C) { chip8InputManager[Chip8Inputs.B] = true }
            up(Key.C) { chip8InputManager[Chip8Inputs.B] = false }

            down(Key.V) { chip8InputManager[Chip8Inputs.F] = true }
            up(Key.V) { chip8InputManager[Chip8Inputs.F] = false }

            // For system operations we only want to detect the initial press, ignoring the release
//            down(Key.F1) {  }
//            if (switchOn) {
//                when (e.keyCode) {
//                    KeyEvent.VK_F1 -> systemActionInputManager.memoryDumpFunction()
//                }
//            }
        }
    }
}