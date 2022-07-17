package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.Khip8
import com.github.dannyrm.khip8.config.ConfigManager
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
import org.koin.core.annotation.Single
import kotlin.reflect.KClass

@Single
class KorgeUi(private val korgeConfigModule: KorgeConfigModule): Ui {
    override suspend fun start() = Korge(Korge.Config(module = korgeConfigModule))
}

@Single
class KorgeConfigModule(private val displayMemory: DisplayMemory,
                        private val inputManager: InputManager,
                        private val khip8: Khip8,
                        private val configManager: ConfigManager): Module() {
    override val size = SizeInt(configManager.frontEndConfig().windowWidth, configManager.frontEndConfig().windowHeight + TOP_UI_HEIGHT.toInt())
    override val title = "Khip-8"
    override val bgcolor =  Colors["#2b2b2b"]
    override val mainScene: KClass<out Scene> = KorgeEmulatorWindow::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { KorgeEmulatorWindow(displayMemory, inputManager, khip8, configManager) }
        mapPrototype { KorgeEmptyMachineWindow(displayMemory, inputManager, khip8) }
    }
}
