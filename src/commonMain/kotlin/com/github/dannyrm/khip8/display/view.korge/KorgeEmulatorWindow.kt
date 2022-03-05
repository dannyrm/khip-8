package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.Khip8
import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.display.view.korge.containers.khip8DisplayContainer
import com.github.dannyrm.khip8.display.view.korge.containers.khip8UiContainer
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.input.Chip8Inputs
import com.soywiz.korev.Key
import com.soywiz.korge.input.KeysEvents
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*

class KorgeEmulatorWindow(private val displayMemory: DisplayMemory, private val chip8InputManager: Chip8InputManager,
                          private val config: Config, private val khip8: Khip8): Scene() {

    override suspend fun Container.sceneInit() {
        khip8DisplayContainer(config, displayMemory)
            .alignTopToBottomOf(khip8UiContainer(khip8))

        keys {
            mapKhip8Key(Key.KP_0, Chip8Inputs.ONE)
            mapKhip8Key(Key.KP_2, Chip8Inputs.TWO)
            mapKhip8Key(Key.KP_3, Chip8Inputs.THREE)
            mapKhip8Key(Key.KP_4, Chip8Inputs.C)
            mapKhip8Key(Key.Q, Chip8Inputs.FOUR)
            mapKhip8Key(Key.W, Chip8Inputs.FIVE)
            mapKhip8Key(Key.E, Chip8Inputs.SIX)
            mapKhip8Key(Key.R, Chip8Inputs.D)
            mapKhip8Key(Key.A, Chip8Inputs.SEVEN)
            mapKhip8Key(Key.S, Chip8Inputs.EIGHT)
            mapKhip8Key(Key.D, Chip8Inputs.NINE)
            mapKhip8Key(Key.F, Chip8Inputs.E)
            mapKhip8Key(Key.Z, Chip8Inputs.A)
            mapKhip8Key(Key.X, Chip8Inputs.ZERO)
            mapKhip8Key(Key.C, Chip8Inputs.B)
            mapKhip8Key(Key.V, Chip8Inputs.F)
        }
    }

    private fun KeysEvents.mapKhip8Key(key: Key, chip8Inputs: Chip8Inputs) {
        down(key) { chip8InputManager[chip8Inputs] = true }
        up(key) { chip8InputManager[chip8Inputs] = false }
    }
}