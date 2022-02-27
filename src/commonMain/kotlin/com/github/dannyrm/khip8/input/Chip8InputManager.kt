package com.github.dannyrm.khip8.input

import com.github.dannyrm.khip8.cpu.Cpu
import com.github.dannyrm.khip8.cpu.CpuState
import com.github.dannyrm.khip8.util.logger

class Chip8InputManager {
    private var chip8InputState: UInt = 0u
    private var chip8LockedInputState: UInt = 0u

    private lateinit var cpu: Cpu

    fun init(cpu: Cpu) {
        this.cpu = cpu
    }

    fun lockInputs() {
        chip8LockedInputState = chip8InputState
    }

    fun isActive(keyNumber: Int): Boolean {
        return (chip8LockedInputState and Chip8Inputs.values()[keyNumber].bitMask) > 0u
    }

    operator fun set(key: Chip8Inputs, isActive: Boolean) {
        chip8InputState = if (isActive) {
            chip8InputState or key.bitMask
        } else {
            chip8InputState and key.bitMask.inv()
        }

        if (isActive) {
            // It might be that the CPU was paused waiting for input, so we set the CPU to "running" to account
            // for this.
            cpu.cpuState = CpuState.RUNNING
        }

        LOG.debug { "Chip 8 key $key: $isActive. New input state: $chip8InputState" }
    }

    companion object {
        private val LOG = logger(this::class)
    }
}

enum class Chip8Inputs(val bitMask: UInt) {
    ZERO(bitMask = 0x1u),
    ONE(bitMask = 0x2u),
    TWO(bitMask = 0x4u),
    THREE(bitMask = 0x8u),
    FOUR(0x10u),
    FIVE(bitMask = 0x20u),
    SIX(bitMask = 0x40u),
    SEVEN(bitMask = 0x80u),
    EIGHT(bitMask = 0x100u),
    NINE(bitMask = 0x200u),
    A(bitMask = 0x400u),
    B(bitMask = 0x800u),
    C(bitMask = 0x1000u),
    D(bitMask = 0x2000u),
    E(bitMask = 0x8000u),
    F(bitMask = 0x10000u)
}
