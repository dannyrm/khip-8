package uk.co.dmatthews.khip8.input

import org.slf4j.Logger
import org.slf4j.LoggerFactory

class Chip8InputManager {
    private var chip8InputState: UInt = 0u
    private var chip8LockedInputState: UInt = 0u

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

        LOG.error("Chip 8 key {}: {}. New input state: {}", key, isActive, chip8InputState)
    }

    companion object {
        private val LOG: Logger = LoggerFactory.getLogger(Chip8InputManager::class.java)
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
