package uk.co.dmatthews.khip8

import com.sksamuel.hoplite.ConfigLoader
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.core.module.Module
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import uk.co.dmatthews.khip8.config.Config
import uk.co.dmatthews.khip8.cpu.Cpu
import uk.co.dmatthews.khip8.cpu.InstructionDecoder
import uk.co.dmatthews.khip8.display.model.Display
import uk.co.dmatthews.khip8.display.model.DisplayMemory
import uk.co.dmatthews.khip8.display.view.SwingUi
import uk.co.dmatthews.khip8.display.view.Ui
import uk.co.dmatthews.khip8.executors.CpuInstructionExecutor
import uk.co.dmatthews.khip8.input.Chip8InputManager
import uk.co.dmatthews.khip8.input.KeyboardManager
import uk.co.dmatthews.khip8.input.SystemActionInputManager
import uk.co.dmatthews.khip8.memory.MemoryManager
import uk.co.dmatthews.khip8.memory.Stack
import uk.co.dmatthews.khip8.memory.TimerRegister
import uk.co.dmatthews.khip8.memory.ValidatedMemory
import uk.co.dmatthews.khip8.sound.SoundGenerator
import uk.co.dmatthews.khip8.sound.SoundTimerRegister
import uk.co.dmatthews.khip8.util.FeatureManager
import uk.co.dmatthews.khip8.util.memoryDump
import java.awt.Canvas
import java.io.File
import kotlin.system.exitProcess

object Khip8Bootstrap: KoinComponent {
    val khip8 by inject<Khip8>()
    private val LOG: Logger = LoggerFactory.getLogger(Khip8Bootstrap::class.java)

    @JvmStatic
    fun main(args: Array<String>) {
        if (args.isEmpty()) {
            println("ERROR: ROM file expected as parameter")
            exitProcess(1)
        }
        boot(File(args[0]))
    }

    fun boot(file: File, overrideModule: Module? = null) {
        loadDependencies(overrideModule)

        khip8.load(file)
        khip8.start()
    }

    private fun loadDependencies(overrideModule: Module? = null) {
        val config = ConfigLoader().loadConfigOrThrow<Config>("/standard.json")

        LOG.info("Loading Config: $config")

        FeatureManager.systemMode = config.systemMode

        val dependencies = module {
            single { DisplayMemory() }
            single { SoundGenerator(config.soundConfig) }
            single<TimerRegister> { SoundTimerRegister(get()) }
            single { InstructionDecoder() }
            single { Cpu(get(), get(), get(), get(), get(), memoryConfig = config.memoryConfig) }
            single { Stack(config.memoryConfig.stackSize) }
            single { ValidatedMemory(config.memoryConfig.memorySize) }
            single { MemoryManager(soundRegister = get(), memoryConfig = config.memoryConfig) }
            single { Khip8(get(), get(), get(), config) }
            single { Canvas() }
            single { Chip8InputManager() }
            single { CpuInstructionExecutor() }
            single { SystemActionInputManager() }
            single<Ui> { SwingUi(get(), get()) }
            single { KeyboardManager(get(), get()) }
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