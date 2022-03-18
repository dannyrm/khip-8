package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.Khip8
import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.display.view.korge.containers.KorgeUiContainer.Companion.TOP_UI_HEIGHT
import com.github.dannyrm.khip8.input.InputManager
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import kotlinx.coroutines.Job
import kotlin.reflect.KClass

class KorgeUi(private val korgeConfigModule: KorgeConfigModule): Ui {
    override suspend fun start(config: Config) = Korge(Korge.Config(module = korgeConfigModule))
}

class KorgeConfigModule(private val displayMemory: DisplayMemory, private val inputManager: InputManager,
                        private val config: Config, private val khip8: Khip8): Module() {
    override val size = SizeInt(config.frontEndConfig.windowWidth, config.frontEndConfig.windowHeight + TOP_UI_HEIGHT.toInt())
    override val title = "Khip-8"
    override val bgcolor =  Colors["#2b2b2b"]
    override val mainScene: KClass<out Scene> = KorgeEmulatorWindow::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { KorgeEmulatorWindow(displayMemory, inputManager, config, khip8) }
        mapPrototype { KorgeEmptyMachineWindow(displayMemory, inputManager, config, khip8) }
    }
}
