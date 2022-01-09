package com.github.dannyrm.khip8.input

import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.EnumSource
import strikt.api.expectThat
import strikt.assertions.isFalse
import strikt.assertions.isTrue
import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.CpuState
import com.github.dannyrm.khip8.input.Chip8InputManager
import com.github.dannyrm.khip8.input.Chip8Inputs

class Chip8InputManagerUnitTest {

    @ParameterizedTest
    @EnumSource(Chip8Inputs::class)
    fun `Single key is pressed`(chip8Input: Chip8Inputs) {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[chip8Input] = true
        chip8InputManager.lockInputs()

        expectThat(chip8InputManager.isActive(chip8Input.ordinal)).isTrue()

        verify(exactly = 1) { cpu.cpuState = CpuState.RUNNING }
    }

    @ParameterizedTest
    @EnumSource(Chip8Inputs::class)
    fun `Single key is pressed then released`(chip8Input: Chip8Inputs) {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[chip8Input] = true
        chip8InputManager[chip8Input] = false
        chip8InputManager.lockInputs()

        expectThat(chip8InputManager.isActive(chip8Input.ordinal)).isFalse()

        verify(exactly = 1) { cpu.cpuState = CpuState.RUNNING }
    }

    @Test
    fun `Multiple keys are pressed`() {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[Chip8Inputs.D] = true
        chip8InputManager[Chip8Inputs.ZERO] = true
        chip8InputManager[Chip8Inputs.EIGHT] = true
        chip8InputManager.lockInputs()

        expectThat(chip8InputManager.isActive(Chip8Inputs.ZERO.ordinal)).isTrue()
        expectThat(chip8InputManager.isActive(Chip8Inputs.ONE.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.TWO.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.THREE.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.FOUR.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.FIVE.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.SIX.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.SEVEN.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.EIGHT.ordinal)).isTrue()
        expectThat(chip8InputManager.isActive(Chip8Inputs.NINE.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.A.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.B.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.C.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.D.ordinal)).isTrue()
        expectThat(chip8InputManager.isActive(Chip8Inputs.E.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.F.ordinal)).isFalse()

        verify(exactly = 3) { cpu.cpuState = CpuState.RUNNING }
    }

    @Test
    fun `Multiple keys are pressed then released`() {
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

        expectThat(chip8InputManager.isActive(Chip8Inputs.ZERO.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.ONE.ordinal)).isTrue()
        expectThat(chip8InputManager.isActive(Chip8Inputs.TWO.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.THREE.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.FOUR.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.FIVE.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.SIX.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.SEVEN.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.EIGHT.ordinal)).isTrue()
        expectThat(chip8InputManager.isActive(Chip8Inputs.NINE.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.A.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.B.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.C.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.D.ordinal)).isTrue()
        expectThat(chip8InputManager.isActive(Chip8Inputs.E.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.F.ordinal)).isTrue()

        verify(exactly = 5) { cpu.cpuState = CpuState.RUNNING }
    }

    @Test
    fun `Pressed keys are not reflected if inputs are not locked`() {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[Chip8Inputs.D] = true
        chip8InputManager[Chip8Inputs.ZERO] = true
        chip8InputManager[Chip8Inputs.EIGHT] = true

        expectThat(chip8InputManager.isActive(Chip8Inputs.ZERO.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.EIGHT.ordinal)).isFalse()
        expectThat(chip8InputManager.isActive(Chip8Inputs.D.ordinal)).isFalse()

        verify(exactly = 3) { cpu.cpuState = CpuState.RUNNING }
    }

    @Test
    fun `Pressed keys after lock are not reflected`() {
        val cpu = mockk<Cpu>(relaxed = true)
        val chip8InputManager = Chip8InputManager()
        chip8InputManager.init(cpu)

        chip8InputManager[Chip8Inputs.D] = true
        chip8InputManager[Chip8Inputs.ZERO] = true
        chip8InputManager[Chip8Inputs.EIGHT] = true
        chip8InputManager.lockInputs()

        expectThat(chip8InputManager.isActive(Chip8Inputs.ZERO.ordinal)).isTrue()
        expectThat(chip8InputManager.isActive(Chip8Inputs.EIGHT.ordinal)).isTrue()
        expectThat(chip8InputManager.isActive(Chip8Inputs.D.ordinal)).isTrue()

        chip8InputManager[Chip8Inputs.D] = false

        expectThat(chip8InputManager.isActive(Chip8Inputs.D.ordinal)).isTrue()

        chip8InputManager.lockInputs()

        expectThat(chip8InputManager.isActive(Chip8Inputs.D.ordinal)).isFalse()

        verify(exactly = 3) { cpu.cpuState = CpuState.RUNNING }
    }
}