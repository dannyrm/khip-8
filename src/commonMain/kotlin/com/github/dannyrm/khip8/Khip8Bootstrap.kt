package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.RunningState.RUNNING
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
                configModules(),
                systemExecutionModules(),
                memoryModules(),
                inputModules(),
                soundModules(),
                displayModules(),
                uiModules(),

                *additionalModules.toTypedArray()
            )
        }
    }

    private fun configModules() =
        module {
            single<ConfigManager> { Chip8PropertiesConfig() }
        }

    private fun systemExecutionModules() =
        module {
            single {
                Cpu(
                    displayMemory = get(), memoryManager = get(), delayRegister = get(), soundRegister = get(),
                    inputManager = get(), memorySize = get<ConfigManager>().memoryConfig().memorySize,
                    cpuState = RUNNING
                )
            }

            single { CpuInstructionExecutor(cpu = get()) }

            single {
                InstructionProcessor(
                    inputManager = get(),
                    memoryManager = get(),
                    instructionExecutors = listOf(get<CpuInstructionExecutor>())
                )
            }

            single {
                Khip8(
                    instructionProcessor = get(), delayRegister = get(), soundRegister = get(),
                    numberOfCpuTicksPerPeripheralTick = get<ConfigManager>().numberOfCpuTicksPerPeripheralTick(),
                    delayBetweenCycles = get<ConfigManager>().delayBetweenCycles()
                )
            }
        }

    private fun memoryModules() =
        module {
            single { Stack(get<ConfigManager>().memoryConfig().stackSize) }
            single(named("ram")) { ValidatedMemory(get<ConfigManager>().memoryConfig().memorySize) }
            single(named("registers")) { ValidatedMemory(get<ConfigManager>().memoryConfig().numberOfGeneralPurposeRegisters) }
            single {
                MemoryManager(
                    get(),
                    get(qualifier = named("ram")),
                    get(qualifier = named("registers")),
                    get<ConfigManager>().memoryConfig().programStartAddress,
                    get<ConfigManager>().memoryConfig().interpreterStartAddress
                )
            }
            single { TimerRegister() }
        }

    private fun inputModules() =
        module {
            single { InputManager() }
        }

    private fun soundModules() =
        module {
            single { runBlockingNoSuspensions { SoundTone(toneFrequency = 2000.0, toneLength = 10_000.0) } }
            single { SoundTimerRegister(get()) }
            single { SoundGenerator(get()) }
        }

    private fun displayModules() =
        module {
            single { DisplayMemory() }
            single { Display(displayMemory = get()) }
        }

    private fun uiModules() =
        module {
            single { KorgeConfigModule(displayMemory = get(), inputManager = get(), khip8 = get(), configManager = get()) }
            single<Ui> { KorgeUi(korgeConfigModule = get()) }
        }

    private fun setupSubscriptions() {
        get<InputManager>().subscribe(get<Cpu>())

        val khip8 = get<Khip8>()

        khip8.subscribe(get<Cpu>())
        khip8.subscribe(get<InstructionProcessor>())
        khip8.subscribe(get<TimerRegister>())
        khip8.subscribe(get<SoundTimerRegister>())

        khip8.subscribe(get<Display>())
        khip8.subscribe(get<MemoryManager>())
    }
}

expect fun lineSeparator(): String
expect fun loadProperties(propertiesInputStream: ByteArrayInputStream?): Settings?

fun logger(klass: KClass<*>) = Logger(klass.qualifiedName ?: "Unknown").setLevel(Logger.Level.INFO)
