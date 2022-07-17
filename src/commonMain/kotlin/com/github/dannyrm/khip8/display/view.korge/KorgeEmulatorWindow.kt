package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.Khip8
import com.github.dannyrm.khip8.config.ConfigManager
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.display.view.korge.containers.khip8DisplayContainer
import com.github.dannyrm.khip8.display.view.korge.containers.khip8UiContainer
import com.github.dannyrm.khip8.input.InputManager
import com.github.dannyrm.khip8.input.KeyboardInput
import com.soywiz.korev.Key
import com.soywiz.korge.input.KeysEvents
import com.soywiz.korge.input.keys
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.*
import com.soywiz.korio.file.std.resourcesVfs

class KorgeEmulatorWindow(private val displayMemory: DisplayMemory,
                          private val inputManager: InputManager,
                          private val khip8: Khip8,
                          private val configManager: ConfigManager): Scene() {
    private lateinit var displayContainer: Container
    private lateinit var uiContainer: Container

    override suspend fun Container.sceneInit() {
        val romFiles = resourcesVfs["c8/"].listSimple()

        displayContainer = khip8DisplayContainer(displayMemory, configManager)
        uiContainer = khip8UiContainer(khip8, romFiles)

        displayContainer
            .alignTopToBottomOf(uiContainer)

        keys {
            mapKhip8Key(Key.KP_0, KeyboardInput.ONE)
            mapKhip8Key(Key.KP_2, KeyboardInput.TWO)
            mapKhip8Key(Key.KP_3, KeyboardInput.THREE)
            mapKhip8Key(Key.KP_4, KeyboardInput.C)
            mapKhip8Key(Key.Q, KeyboardInput.FOUR)
            mapKhip8Key(Key.W, KeyboardInput.FIVE)
            mapKhip8Key(Key.E, KeyboardInput.SIX)
            mapKhip8Key(Key.R, KeyboardInput.D)
            mapKhip8Key(Key.A, KeyboardInput.SEVEN)
            mapKhip8Key(Key.S, KeyboardInput.EIGHT)
            mapKhip8Key(Key.D, KeyboardInput.NINE)
            mapKhip8Key(Key.F, KeyboardInput.E)
            mapKhip8Key(Key.Z, KeyboardInput.A)
            mapKhip8Key(Key.X, KeyboardInput.ZERO)
            mapKhip8Key(Key.C, KeyboardInput.B)
            mapKhip8Key(Key.V, KeyboardInput.F)
        }
    }


    private fun KeysEvents.mapKhip8Key(key: Key, keyboardInput: KeyboardInput) {
        down(key) { inputManager[keyboardInput] = true }
        up(key) { inputManager[keyboardInput] = false }
    }
}