package com.github.dannyrm.khip8.input

import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.observers.events.InputEvent
import io.kotest.core.spec.style.FunSpec
import io.kotest.datatest.withData
import io.mockk.mockk
import io.mockk.verify
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class InputManagerUnitTest: FunSpec({

    context("Single key is pressed") {
        withData(KeyboardInput.values().asSequence()) { chip8Input: KeyboardInput ->
            val cpu = mockk<Cpu>(relaxed = true)
            val inputManager = InputManager()
            inputManager.subscribe(cpu)

            inputManager[chip8Input] = true
            inputManager.lockInputs()

            assertTrue { inputManager.isActive(chip8Input.ordinal) }

            verify(exactly = 1) { cpu.receiveEvent(InputEvent(chip8Input, true)) }
        }
    }

    context("Single key is pressed then released") {
        withData(KeyboardInput.values().asSequence()) { chip8Input: KeyboardInput ->
            val cpu = mockk<Cpu>(relaxed = true)
            val inputManager = InputManager()
            inputManager.subscribe(cpu)

            inputManager[chip8Input] = true
            inputManager[chip8Input] = false
            inputManager.lockInputs()

            assertFalse { inputManager.isActive(chip8Input.ordinal) }

            verify(exactly = 1) { cpu.receiveEvent(InputEvent(chip8Input, true)) }
        }
    }

    test("Multiple keys are pressed") {
        val cpu = mockk<Cpu>(relaxed = true)
        val inputManager = InputManager()
        inputManager.subscribe(cpu)

        inputManager[KeyboardInput.D] = true
        inputManager[KeyboardInput.ZERO] = true
        inputManager[KeyboardInput.EIGHT] = true
        inputManager.lockInputs()

        assertTrue { inputManager.isActive(KeyboardInput.ZERO.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.ONE.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.TWO.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.THREE.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.FOUR.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.FIVE.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.SIX.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.SEVEN.ordinal) }
        assertTrue { inputManager.isActive(KeyboardInput.EIGHT.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.NINE.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.A.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.B.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.C.ordinal) }
        assertTrue { inputManager.isActive(KeyboardInput.D.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.E.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.F.ordinal) }

        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.D, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.ZERO, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.EIGHT, true)) }
    }

    test("Multiple keys are pressed then released") {
        val cpu = mockk<Cpu>(relaxed = true)
        val inputManager = InputManager()
        inputManager.subscribe(cpu)

        inputManager[KeyboardInput.D] = true
        inputManager[KeyboardInput.ZERO] = true
        inputManager[KeyboardInput.EIGHT] = true
        inputManager[KeyboardInput.ZERO] = false
        inputManager[KeyboardInput.F] = true
        inputManager[KeyboardInput.ONE] = true
        inputManager.lockInputs()

        assertFalse { inputManager.isActive(KeyboardInput.ZERO.ordinal) }
        assertTrue { inputManager.isActive(KeyboardInput.ONE.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.TWO.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.THREE.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.FOUR.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.FIVE.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.SIX.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.SEVEN.ordinal) }
        assertTrue { inputManager.isActive(KeyboardInput.EIGHT.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.NINE.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.A.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.B.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.C.ordinal) }
        assertTrue { inputManager.isActive(KeyboardInput.D.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.E.ordinal) }
        assertTrue { inputManager.isActive(KeyboardInput.F.ordinal) }

        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.D, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.ZERO, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.EIGHT, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.ZERO, false)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.F, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.ONE, true)) }
    }

    test("Pressed keys are not reflected if inputs are not locked") {
        val cpu = mockk<Cpu>(relaxed = true)
        val inputManager = InputManager()
        inputManager.subscribe(cpu)

        inputManager[KeyboardInput.D] = true
        inputManager[KeyboardInput.ZERO] = true
        inputManager[KeyboardInput.EIGHT] = true

        assertFalse { inputManager.isActive(KeyboardInput.ZERO.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.EIGHT.ordinal) }
        assertFalse { inputManager.isActive(KeyboardInput.D.ordinal) }

        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.D, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.ZERO, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.EIGHT, true)) }
    }

    test("Pressed keys after lock are not reflected") {
        val cpu = mockk<Cpu>(relaxed = true)
        val inputManager = InputManager()
        inputManager.subscribe(cpu)

        inputManager[KeyboardInput.D] = true
        inputManager[KeyboardInput.ZERO] = true
        inputManager[KeyboardInput.EIGHT] = true
        inputManager.lockInputs()

        assertTrue { inputManager.isActive(KeyboardInput.ZERO.ordinal) }
        assertTrue { inputManager.isActive(KeyboardInput.EIGHT.ordinal) }
        assertTrue { inputManager.isActive(KeyboardInput.D.ordinal) }

        inputManager[KeyboardInput.D] = false

        assertTrue { inputManager.isActive(KeyboardInput.D.ordinal) }

        inputManager.lockInputs()

        assertFalse { inputManager.isActive(KeyboardInput.D.ordinal) }

        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.D, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.ZERO, true)) }
        verify(exactly = 1) { cpu.receiveEvent(InputEvent(KeyboardInput.EIGHT, true)) }
    }
})