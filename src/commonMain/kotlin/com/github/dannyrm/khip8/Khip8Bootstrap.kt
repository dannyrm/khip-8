package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.config.FrontEndType
import com.github.dannyrm.khip8.config.delayBetweenCycles
import com.github.dannyrm.khip8.config.numberOfCpuTicksPerPeripheralTick
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.InstructionDecoder
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.display.view.korge.KorgeConfigModule
import com.github.dannyrm.khip8.display.view.korge.KorgeUi
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
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoJs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.job
import org.koin.core.component.get
import org.koin.core.module.Module

object Khip8Bootstrap: KoinComponent {
    private val LOG = logger(this::class)

    fun boot(filePath: String, config: Config, additionalModules: List<Module>) {
        LOG.info { "Loading Config: $config" }

        loadDependencies(additionalModules, config)

        val khip8 = get<Khip8>()
        val ui = get<Ui>()

        khip8.load(filePath)

        // Starts the Chip-8 execution and UI execution in separate Co-routines
        runBlockingNoJs {
            launch(Dispatchers.Default) {
                val cpuTicksPerPeripheralTick = numberOfCpuTicksPerPeripheralTick(config)
                val (delayInMillis, delayInNanos) = delayBetweenCycles(config)

                while (true) {
                    khip8.execute(cpuTicksPerPeripheralTick, delayInMillis)
                }
            }

            launch(Dispatchers.Default) {
                ui.start(config, this.coroutineContext.job)
            }
        }
    }

    private fun loadDependencies(additionalModules: List<Module>, config: Config) {
        FeatureManager.systemMode = config.systemMode

        val dependencies = module {
            single<TimerRegister> { SoundTimerRegister(get()) }
            single { InstructionDecoder() }
            single { Cpu(get(), get(), get(), get(), get(), memoryConfig = config.memoryConfig) }
            single { Stack(config.memoryConfig.stackSize) }
            single { ValidatedMemory(config.memoryConfig.memorySize) }
            single { MemoryManager(soundRegister = get(), memoryConfig = config.memoryConfig) }
            single { Khip8(get(), get(), get()) }
            single { CpuInstructionExecutor() }
            single { SystemActionInputManager() }
            single { DisplayMemory() }
            single { Display(get()) }
            single { Chip8InputManager() }

            if (config.frontEndConfig.frontEnd == FrontEndType.KORGE) {
                single { KorgeConfigModule(get(), get(), config, get()) }
                single<Ui> { KorgeUi(get()) }
            }
        }

        startKoin {
            modules(dependencies)
            additionalModules.forEach {
                modules(it)
            }

            val cpu = koin.get<Cpu>()
            val chip8InputManager = koin.get<Chip8InputManager>()
            val cpuInstructionExecutor = koin.get<CpuInstructionExecutor>()

            val memoryManager = koin.get<MemoryManager>()
            val display = koin.get<Display>()
            val systemActionInputManager = koin.get<SystemActionInputManager>()

            chip8InputManager.init(cpu)
            cpuInstructionExecutor.init(cpu)

            systemActionInputManager.memoryDumpFunction = {
                memoryDump(memoryManager.toString() + display.toString())
            }
        }
    }
}