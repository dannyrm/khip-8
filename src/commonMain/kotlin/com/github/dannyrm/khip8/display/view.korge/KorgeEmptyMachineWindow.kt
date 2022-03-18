package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.Khip8
import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.input.InputManager
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.view.Container

class KorgeEmptyMachineWindow(private val displayMemory: DisplayMemory, private val inputManager: InputManager,
                              private val config: Config, private val khip8: Khip8): Scene() {

    override suspend fun Container.sceneInit() {
        setupRunLoop(this)
    }

    private fun setupRunLoop(container: Container) {

    }
}