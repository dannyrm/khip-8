package com.github.dannyrm.khip8.input

import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.CpuState
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class Chip8InputManagerUnitTest: FunSpec({

    context("Single key is pressed") {
        withData(Chip8Inputs.values().asSequence()) { chip8Input: Chip8Inputs ->
            val cpu = mockk<Cpu>(relaxed = true)
            val chip8InputManager = Chip8InputManager()
            chip8InputManager.init(cpu)

            chip8InputManager[chip8Input] = true
            chip8InputManager.lockInputs()

            assertTrue { chip8InputManager.isActive(chip8Input.ordinal) }

            verify(exactly = 1) { cpu.cpuState = CpuState.RUNNING }
        }
    }

    context("Single key is pressed then released") {
        withData(Chip8Inputs.values().asSequence()) { chip8Input: Chip8Inputs ->
            val cpu = mockk<Cpu>(relaxed = true)
            val chip8InputManager = Chip8InputManager()
            chip8InputManager.init(cpu)

            chip8InputManager[chip8Input] = true
            chip8InputManager[chip8Input] = false
            chip8InputManager.lockInputs()

            assertFalse { chip8InputManager.isActive(chip8Input.ordinal) }

            verify(exactly = 1) { cpu.cpuState = CpuState.RUNNING }
        }
    }

    test("Multiple keys are pressed") {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[Chip8Inputs.D] = true
        chip8InputManager[Chip8Inputs.ZERO] = true
        chip8InputManager[Chip8Inputs.EIGHT] = true
        chip8InputManager.lockInputs()

        assertTrue { chip8InputManager.isActive(Chip8Inputs.ZERO.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.ONE.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.TWO.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.THREE.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.FOUR.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.FIVE.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.SIX.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.SEVEN.ordinal) }
        assertTrue { chip8InputManager.isActive(Chip8Inputs.EIGHT.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.NINE.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.A.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.B.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.C.ordinal) }
        assertTrue { chip8InputManager.isActive(Chip8Inputs.D.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.E.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.F.ordinal) }

        verify(exactly = 3) { cpu.cpuState = CpuState.RUNNING }
    }

    test("Multiple keys are pressed then released") {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[Chip8Inputs.D] = true
        chip8InputManager[Chip8Inputs.ZERO] = true
        chip8InputManager[Chip8Inputs.EIGHT] = true
        chip8InputManager[Chip8Inputs.ZERO] = false
        chip8InputManager[Chip8Inputs.F] = true
        chip8InputManager[Chip8Inputs.ONE] = true
        chip8InputManager.lockInputs()

        assertFalse { chip8InputManager.isActive(Chip8Inputs.ZERO.ordinal) }
        assertTrue { chip8InputManager.isActive(Chip8Inputs.ONE.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.TWO.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.THREE.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.FOUR.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.FIVE.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.SIX.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.SEVEN.ordinal) }
        assertTrue { chip8InputManager.isActive(Chip8Inputs.EIGHT.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.NINE.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.A.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.B.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.C.ordinal) }
        assertTrue { chip8InputManager.isActive(Chip8Inputs.D.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.E.ordinal) }
        assertTrue { chip8InputManager.isActive(Chip8Inputs.F.ordinal) }

        verify(exactly = 5) { cpu.cpuState = CpuState.RUNNING }
    }

    test("Pressed keys are not reflected if inputs are not locked") {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[Chip8Inputs.D] = true
        chip8InputManager[Chip8Inputs.ZERO] = true
        chip8InputManager[Chip8Inputs.EIGHT] = true

        assertFalse { chip8InputManager.isActive(Chip8Inputs.ZERO.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.EIGHT.ordinal) }
        assertFalse { chip8InputManager.isActive(Chip8Inputs.D.ordinal) }

        verify(exactly = 3) { cpu.cpuState = CpuState.RUNNING }
    }

    test("Pressed keys after lock are not reflected") {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[Chip8Inputs.D] = true
        chip8InputManager[Chip8Inputs.ZERO] = true
        chip8InputManager[Chip8Inputs.EIGHT] = true
        chip8InputManager.lockInputs()

        assertTrue { chip8InputManager.isActive(Chip8Inputs.ZERO.ordinal) }
        assertTrue { chip8InputManager.isActive(Chip8Inputs.EIGHT.ordinal) }
        assertTrue { chip8InputManager.isActive(Chip8Inputs.D.ordinal) }

        chip8InputManager[Chip8Inputs.D] = false

        assertTrue { chip8InputManager.isActive(Chip8Inputs.D.ordinal) }

        chip8InputManager.lockInputs()

        assertFalse { chip8InputManager.isActive(Chip8Inputs.D.ordinal) }

        verify(exactly = 3) { cpu.cpuState = CpuState.RUNNING }
    }
})