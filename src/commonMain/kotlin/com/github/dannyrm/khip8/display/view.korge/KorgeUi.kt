package com.github.dannyrm.khip8.display.view.korge

import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.soywiz.korge.Korge
import com.soywiz.korge.scene.Module
import com.soywiz.korge.scene.Scene
import com.soywiz.korim.color.Colors
import com.soywiz.korinject.AsyncInjector
import com.soywiz.korma.geom.SizeInt
import kotlinx.coroutines.Job
import kotlin.reflect.KClass

class KorgeUi(private val korgeConfigModule: KorgeConfigModule): Ui {
    override suspend fun start(config: Config, rootJob: Job) = Korge(Korge.Config(module = korgeConfigModule))
}

class KorgeConfigModule(private val displayMemory: DisplayMemory, private val chip8InputManager: Chip8InputManager, private val config: Config): Module() {
    override val size = SizeInt(config.frontEndConfig.windowWidth, config.frontEndConfig.windowHeight+30)
    override val title = "Khip-8"
    override val bgcolor =  Colors["#2b2b2b"]
    override val mainScene: KClass<out Scene> = KorgeEmulatorWindow::class

    override suspend fun AsyncInjector.configure() {
        mapPrototype { KorgeEmulatorWindow(displayMemory, chip8InputManager, config) }
    }
}