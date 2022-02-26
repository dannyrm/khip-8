package com.github.dannyrm.khip8

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.github.dannyrm.khip8.config.loadConfig
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.InstructionDecoder
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.executors.CpuInstructionExecutor
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.input.SystemActionInputManager
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.Stack
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.memory.ValidatedMemory
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.github.dannyrm.khip8.util.FeatureManager
import com.github.dannyrm.khip8.util.logger
import com.github.dannyrm.khip8.util.memoryDump
import org.koin.core.module.Module

object Khip8Bootstrap: KoinComponent {
    val khip8 by inject<Khip8>()
    private val LOG = logger(this::class)

    fun boot(filePath: String, additionalModules: List<Module>) {
        loadDependencies(additionalModules)

        khip8.load(filePath)
        khip8.start()
    }

    private fun loadDependencies(additionalModules: List<Module>) {
        val config = loadConfig()

        LOG.info { "Loading Config: $config" }

        FeatureManager.systemMode = config.systemMode

        val dependencies = module {
            single { DisplayMemory() }
            single<TimerRegister> { SoundTimerRegister(get()) }
            single { InstructionDecoder() }
            single { Cpu(get(), get(), get(), get(), get(), memoryConfig = config.memoryConfig) }
            single { Stack(config.memoryConfig.stackSize) }
            single { ValidatedMemory(config.memoryConfig.memorySize) }
            single { MemoryManager(soundRegister = get(), memoryConfig = config.memoryConfig) }
            single { Khip8(get(), get(), get(), config) }
            single { Chip8InputManager() }
            single { CpuInstructionExecutor() }
            single { SystemActionInputManager() }
            single { Display(get(), get()) }
        }

        startKoin {
            modules(dependencies)
            additionalModules.forEach {
                modules(it)
            }

            val cpu = koin.get<Cpu>()
            val chip8InputManager = koin.get<Chip8InputManager>()
            val cpuInstructionExecutor = koin.get<CpuInstructionExecutor>()
            val khip8 = koin.get<Khip8>()
            val ui = koin.get<Ui>()

            val memoryManager = koin.get<MemoryManager>()
            val display = koin.get<Display>()
            val systemActionInputManager = koin.get<SystemActionInputManager>()

            ui.init(config, khip8::halt)
            chip8InputManager.init(cpu)
            cpuInstructionExecutor.init(cpu)

            systemActionInputManager.memoryDumpFunction = {
                memoryDump(memoryManager.toString() + display.toString())
            }
        }
    }
}