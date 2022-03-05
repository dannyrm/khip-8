package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.Khip8
import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.display.view.korge.containers.KorgeUiContainer
import com.github.dannyrm.khip8.display.view.korge.containers.KorgeUiContainer.Companion.TOP_UI_HEIGHT
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.util.calculatePixelSize
import com.soywiz.klock.Frequency
import com.soywiz.korge.Korge
import com.soywiz.korge.input.onClick
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.*
import com.soywiz.korim.color.Colors
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import com.soywiz.korma.geom.vector.rect
import kotlinx.coroutines.Job
import kotlin.reflect.KClass

class KorgeUi(private val korgeConfigModule: KorgeConfigModule): Ui {
    override suspend fun start(config: Config, rootJob: Job) = Korge(Korge.Config(module = korgeConfigModule))
}

class KorgeConfigModule(private val displayMemory: DisplayMemory, private val chip8InputManager: Chip8InputManager,
                        private val config: Config, private val khip8: Khip8): Module() {
    override val size = SizeInt(config.frontEndConfig.windowWidth, config.frontEndConfig.windowHeight + TOP_UI_HEIGHT.toInt())
    override val title = "Khip-8"
    override val bgcolor =  Colors["#2b2b2b"]
    override val mainScene: KClass<out Scene> = KorgeEmulatorWindow::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { KorgeEmulatorWindow(displayMemory, chip8InputManager, config, khip8) }
        mapPrototype { KorgeEmptyMachineWindow(displayMemory, chip8InputManager, config, khip8) }
    }
}
