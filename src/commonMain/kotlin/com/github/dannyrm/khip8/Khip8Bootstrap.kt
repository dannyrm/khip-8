package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.Khip8State.RUNNING
import com.github.dannyrm.khip8.Khip8State.STOPPED
import com.github.dannyrm.khip8.config.Chip8PropertiesConfig
import com.github.dannyrm.khip8.config.ConfigManager
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
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import org.koin.core.component.get
import org.koin.core.module.Module
import org.koin.core.qualifier.Qualifier
import org.koin.core.qualifier.QualifierValue
import org.koin.core.qualifier.named
import org.koin.core.qualifier.qualifier
import java.io.ByteArrayInputStream
import kotlin.reflect.KClass

@OptIn(ExperimentalUnsignedTypes::class)
object Khip8Bootstrap: KoinComponent {
    fun boot(additionalModules: List<Module>) {
        val soundToneLength = 10_000.0

        startKoin {
            modules(
                module {
                    // Config
                    single<ConfigManager> { Chip8PropertiesConfig() }

                    // System execution
                    single { Khip8Status(khip8State = STOPPED, loadedRom = null) }
                    single {
                        Cpu(
                            instructionDecoder = get(),
                            displayMemory = get(),
                            memoryManager = get(),
                            delayRegister = get(),
                            soundRegister = get(),
                            inputManager = get(),
                            get<ConfigManager>().memoryConfig().memorySize,
                            cpuState = RUNNING) }
                    single {
                        Khip8(
                            cpu = get(),
                            memoryManager = get(),
                            display = get(),
                            delayRegister = get(),
                            soundRegister = get(),
                            khip8Status = get(),
                            numberOfCpuTicksPerPeripheralTick = get<ConfigManager>().numberOfCpuTicksPerPeripheralTick(),
                            delayBetweenCycles = get<ConfigManager>().delayBetweenCycles())
                    }
                    single { CpuInstructionExecutor(cpu = get()) }
                    single { InstructionDecoder(instructionExecutors = listOf(get())) }

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
                    single { SystemActionInputManager() }

                    // Sound
                    single { runBlockingNoSuspensions { SoundTone(get(), soundToneLength) } }
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

        val inputManager = get<InputManager>()
        val khip8 = get<Khip8>()
        val ui = get<Ui>()

        inputManager.subscribe(khip8)

        khip8.reset()

        // Starts the Chip-8 execution and UI execution in separate Co-routines
        runBlockingNoJs {
            khip8.execute()

            launch(Dispatchers.Default) {
                ui.start()
            }
        }
    }
}

expect fun lineSeparator(): String
expect fun loadProperties(propertiesInputStream: ByteArrayInputStream?): Settings?

fun logger(klass: KClass<*>) = Logger(klass.qualifiedName ?: "Unknown").setLevel(Logger.Level.INFO)