package com.github.dannyrm.khip8

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.core.module.Module
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
import kotlin.jvm.JvmStatic

object Khip8Bootstrap: KoinComponent {
    val khip8 by inject<Khip8>()
    private val LOG = logger(this::class)

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isNotEmpty()) {
            boot(args[0])
        }

        println("ERROR: ROM file expected as parameter")
    }

    fun boot(filePath: String, overrideModule: Module? = null) {
        loadDependencies(overrideModule)

        khip8.load(filePath)
        khip8.start()
    }

    private fun loadDependencies(overrideModule: Module? = null) {
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
            overrideModule?.apply {
                modules(overrideModule)
            }

            val cpu = koin.get<Cpu>()
            val chip8InputManager = koin.get<Chip8InputManager>()
            val cpuInstructionExecutor = koin.get<CpuInstructionExecutor>()
            val khip8 = koin.get<Khip8>()
            val ui = koin.get<Ui>()

            val memoryManager = koin.get<MemoryManager>()
            val display = koin.get<Display>()
            val systemActionInputManager = koin.get<SystemActionInputManager>()

            chip8InputManager.init(cpu)
            cpuInstructionExecutor.init(cpu)
            ui.init(khip8::halt)

            systemActionInputManager.memoryDumpFunction = {
                memoryDump(memoryManager.toString() + display.toString())
            }
        }
    }
}