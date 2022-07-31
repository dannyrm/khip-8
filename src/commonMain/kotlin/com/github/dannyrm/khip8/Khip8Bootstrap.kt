package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.RunningState.RUNNING
import com.github.dannyrm.khip8.RunningState.STOPPED
import com.github.dannyrm.khip8.config.Chip8PropertiesConfig
import com.github.dannyrm.khip8.config.ConfigManager
import org.koin.core.component.KoinComponent
import org.koin.core.context.startKoin
import org.koin.dsl.module
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.InstructionProcessor
import com.github.dannyrm.khip8.display.model.Display
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.display.view.korge.KorgeConfigModule
import com.github.dannyrm.khip8.display.view.korge.KorgeUi
import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.executors.CpuInstructionExecutor
import com.github.dannyrm.khip8.input.InputManager
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.Stack
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.memory.ValidatedMemory
import com.github.dannyrm.khip8.sound.SoundGenerator
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import com.github.dannyrm.khip8.sound.SoundTone
import com.russhwolf.settings.Settings
import com.soywiz.klogger.Logger
import com.soywiz.klogger.setLevel
import com.soywiz.korio.async.launch
import com.soywiz.korio.async.runBlockingNoJs
import com.soywiz.korio.async.runBlockingNoSuspensions
import kotlinx.coroutines.Dispatchers
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass

object Khip8Bootstrap: KoinComponent {

    fun boot(additionalModules: List<Module>) {
        setupDependencies(additionalModules)
        setupSubscriptions()

        // Starts the Chip-8 execution and UI execution in separate Co-routines
        runBlockingNoJs {
            get<Khip8>().execute()

            launch(Dispatchers.Default) {
                get<Ui>().start()
            }
        }
    }

    private fun setupDependencies(additionalModules: List<Module>) {
        startKoin {
            modules(
                module {
                    // Config
                    single<ConfigManager> { Chip8PropertiesConfig() }

                    // System execution
                    single {
                        Cpu(
                            displayMemory = get(),
                            memoryManager = get(),
                            delayRegister = get(),
                            soundRegister = get(),
                            inputManager = get(),
                            memorySize = get<ConfigManager>().memoryConfig().memorySize,
                            cpuState = RUNNING,
                            khip8RunningState = RUNNING)
                    }
                    single { CpuInstructionExecutor(cpu = get()) }
                    single {
                        InstructionProcessor(
                            inputManager = get(),
                            memoryManager = get(),
                            khip8RunningState = RUNNING,
                            instructionExecutors = listOf(get<CpuInstructionExecutor>()))
                    }
                    single {
                        Khip8(
                            instructionProcessor = get(),
                            delayRegister = get(),
                            soundRegister = get(),
                            numberOfCpuTicksPerPeripheralTick = get<ConfigManager>().numberOfCpuTicksPerPeripheralTick(),
                            delayBetweenCycles = get<ConfigManager>().delayBetweenCycles())
                    }

                    // Memory
                    single { Stack(get<ConfigManager>().memoryConfig().stackSize) }
                    single(named("ram")) { ValidatedMemory(get<ConfigManager>().memoryConfig().memorySize) }
                    single(named("registers")) { ValidatedMemory(get<ConfigManager>().memoryConfig().numberOfGeneralPurposeRegisters) }
                    single {
                        MemoryManager(get(),
                            get(qualifier = named("ram")),
                            get(qualifier = named("registers")),
                            get<ConfigManager>().memoryConfig().programStartAddress,
                            get<ConfigManager>().memoryConfig().interpreterStartAddress)
                    }
                    single { TimerRegister() }

                    // Input
                    single { InputManager() }

                    // Sound
                    single { runBlockingNoSuspensions { SoundTone(toneFrequency = 2000.0, toneLength = 10_000.0) } }
                    single { SoundTimerRegister(get()) }
                    single { SoundGenerator(get()) }

                    // Display
                    single { DisplayMemory() }
                    single { Display(get()) }

                    // UI
                    single { KorgeConfigModule(get(), get(), get(), get()) }
                    single<Ui> { KorgeUi(get()) }
                },

                *additionalModules.toTypedArray()
            )
        }
    }

    private fun setupSubscriptions() {
        val inputManager = get<InputManager>()
        val khip8 = get<Khip8>()
        val cpu = get<Cpu>()
        val instructionProcessor = get<InstructionProcessor>()
        val timerRegister = get<TimerRegister>()
        val soundTimerRegister = get<SoundTimerRegister>()
        val display = get<Display>()
        val memoryManager = get<MemoryManager>()

        inputManager.subscribe(cpu)

        khip8.subscribe(cpu)
        khip8.subscribe(instructionProcessor)
        khip8.subscribe(timerRegister)
        khip8.subscribe(soundTimerRegister)

        khip8.subscribe(display)
        khip8.subscribe(memoryManager)
    }
}

expect fun lineSeparator(): String
expect fun loadProperties(propertiesInputStream: ByteArrayInputStream?): Settings?

fun logger(klass: KClass<*>) = Logger(klass.qualifiedName ?: "Unknown").setLevel(Logger.Level.INFO)