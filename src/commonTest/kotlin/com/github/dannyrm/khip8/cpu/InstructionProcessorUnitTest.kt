package com.github.dannyrm.khip8.cpu

import com.github.dannyrm.khip8.RunningState.RUNNING
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.github.dannyrm.khip8.executors.CpuInstructionExecutor
import com.github.dannyrm.khip8.input.InputManager
import com.github.dannyrm.khip8.memory.MemoryManager
import com.github.dannyrm.khip8.memory.TimerRegister
import com.github.dannyrm.khip8.sound.SoundTimerRegister
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.Test

class InstructionProcessorUnitTest {

    @Test
    fun `tick works correctly`() {
        val nextInstruction: UInt = 0xE6A1u

        val inputManager = mockk<InputManager>(relaxed = true)
        val memoryManager = mockk<MemoryManager>(relaxed = true)
        val displayMemory = mockk<DisplayMemory>(relaxed = true)
        val delayRegister = mockk<TimerRegister>(relaxed = true)
        val soundRegister = mockk<SoundTimerRegister>(relaxed = true)

        val cpu = Cpu(displayMemory, memoryManager, delayRegister, soundRegister, inputManager, memorySize = 4096, cpuState = RUNNING, khip8RunningState = RUNNING)

        val cpuInstructionExecutor = CpuInstructionExecutor(cpu)

        val instructionProcessor = InstructionProcessor(
            inputManager,
            memoryManager,
            khip8RunningState = RUNNING,
            listOf(cpuInstructionExecutor)
        )

        every { memoryManager.fetchNextInstruction() } returns nextInstruction

        instructionProcessor.tick()

        verify { inputManager.lockInputs() }
        verify { memoryManager.fetchNextInstruction() }
        verify { instructionProcessor.decodeAndExecute(nextInstruction) }
    }
}
