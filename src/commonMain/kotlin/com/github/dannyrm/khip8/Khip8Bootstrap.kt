package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.Khip8State.RUNNING
import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.config.buildConfig
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
import com.github.dannyrm.khip8.input.InputManager
import com.github.dannyrm.khip8.input.SystemActionInputManager
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.Stack
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.memory.ValidatedMemory
import com.github.dannyrm.khip8.sound.SoundGenerator
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.github.dannyrm.khip8.sound.SoundTone
import com.github.dannyrm.khip8.util.FeatureManager
import com.russhwolf.settings.Settings
import com.soywiz.klogger.Logger
import com.soywiz.klogger.setLevel
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.async.runBlockingNoSuspensions
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.get
import org.koin.core.module.Module
import kotlin.reflect.KClass

object Khip8Bootstrap: KoinComponent {
    private val LOG = logger(this::class)

    fun boot(settings: Settings, additionalModules: List<Module>) {
        val config = buildConfig(settings)

        LOG.info { "Loading Config: $config" }

        loadDependencies(additionalModules, config)

        val khip8 = get<Khip8>()
        val ui = get<Ui>()

        khip8.reset()

        // Starts the Chip-8 execution and UI execution in separate Co-routines
        runBlockingNoJs {
            val cpuTicksPerPeripheralTick = numberOfCpuTicksPerPeripheralTick(config)
            val delayInMillis = delayBetweenCycles(config)

            khip8.execute(cpuTicksPerPeripheralTick, delayInMillis)

            launch(Dispatchers.Default) {
                ui.start(config)
            }
        }
    }

    private fun loadDependencies(additionalModules: List<Module>, config: Config) {
        FeatureManager.systemMode = config.systemMode

        val khip8Status = Khip8Status(khip8State = Khip8State.STOPPED)

        val soundTone = runBlockingNoSuspensions {
            SoundTone(config.soundConfig.toneFrequency)
        }

        val dependencies = module {
            single { SoundTimerRegister(get()) }
            single { TimerRegister() }
            single { InstructionDecoder() }
            single { Cpu(get(), get(), get(), get(), get(), get(), config.memoryConfig, RUNNING) }
            single { Stack(config.memoryConfig.stackSize) }
            single { ValidatedMemory(config.memoryConfig.memorySize) }
            single { MemoryManager(memoryConfig = config.memoryConfig) }
            single { InputManager() }
            single { SoundGenerator(soundTone) }
            single { Khip8(get(), get(), get(), get(), get(), khip8Status) }
            single { SystemActionInputManager() }
            single { DisplayMemory() }
            single { Display(get()) }

            single { KorgeConfigModule(get(), get(), config, get()) }
            single<Ui> { KorgeUi(get()) }
        }

        startKoin {
            modules(dependencies)
            additionalModules.forEach {
                modules(it)
            }

            val inputManager = koin.get<InputManager>()
            val khip8 = koin.get<Khip8>()

            inputManager.subscribe(khip8)
        }
    }
}

typealias FileAbsolutePath = String
expect fun lineSeparator(): FileAbsolutePath

fun logger(klass: KClass<*>) = Logger(klass.qualifiedName ?: "Unknown").setLevel(Logger.Level.INFO)