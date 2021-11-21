package uk.co.dmatthews.khip8

import org.koin.core.component.KoinComponent
import org.koin.core.component.inject
import org.koin.core.context.startKoin
import org.koin.dsl.module
import org.koin.core.module.Module
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
import java.awt.Canvas
import java.io.File

object Khip8Bootstrap: KoinComponent {

    val khip8 by inject<Khip8>()

    fun boot(file: File, overrideModule: Module? = null) {
        loadDependencies(overrideModule)

        khip8.load(file)
        khip8.start()
    }

    private fun loadDependencies(overrideModule: Module? = null) {
        val dependencies = module {
            single { DisplayMemory() }
            single { InstructionDecoder() }
            single { Cpu(get(), get(), get(), get(), get()) }
            single { Display(get(), get()) }
            single { MemoryManager() }
            single { SystemActionInputManager() }
            single { Khip8(get(), get(), get(), get()) }
            single { Canvas() }
            single { KeyboardManager(get(), get()) }
            single { Chip8InputManager() }
            single { CpuInstructionExecutor() }
            single<Ui> { SwingUi(get(), get()) }
        }

        startKoin {
            modules(dependencies)
            overrideModule?.apply {
                modules(overrideModule)
            }
        }
    }
}

fun main(args: Array<String>) {
    Khip8Bootstrap.boot(File(args[0]))
}