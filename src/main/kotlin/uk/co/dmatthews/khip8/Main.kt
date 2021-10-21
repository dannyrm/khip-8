package uk.co.dmatthews.khip8

import uk.co.dmatthews.khip8.input.Chip8InputManager
import uk.co.dmatthews.khip8.input.KeyboardManager
import uk.co.dmatthews.khip8.display.Display
import org.koin.core.context.startKoin
import org.koin.dsl.module
import uk.co.dmatthews.khip8.cpu.Cpu
import uk.co.dmatthews.khip8.cpu.InstructionDecoder
import uk.co.dmatthews.khip8.display.DisplayMemory
import uk.co.dmatthews.khip8.display.SwingUi
import uk.co.dmatthews.khip8.display.Ui
import uk.co.dmatthews.khip8.executors.CpuInstructionExecutor
import uk.co.dmatthews.khip8.input.SystemActionInputManager
import uk.co.dmatthews.khip8.memory.MemoryManager
import java.awt.Canvas
import java.io.File

fun main(args: Array<String>) {
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

    val dependencyManager = startKoin {
        modules(dependencies)
    }

    val khip8: Khip8 = dependencyManager.koin.get()

    khip8.load(File(args[0]))
    khip8.start()
}