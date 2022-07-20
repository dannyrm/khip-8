package com.github.dannyrm.khip8.input

import com.github.dannyrm.khip8.Khip8Status
import com.github.dannyrm.khip8.input.event.InputEvent
import com.github.dannyrm.khip8.input.event.InputObserver
import com.github.dannyrm.khip8.logger
import org.koin.core.annotation.Single

@Single
class InputManager {
    private var chip8InputState: UInt = 0u
    private var chip8LockedInputState: UInt = 0u
    private val observers: MutableList<InputObserver> = mutableListOf()

    fun lockInputs() {
        chip8LockedInputState = chip8InputState
    }

    fun isActive(keyNumber: Int): Boolean {
        return (chip8LockedInputState and KeyboardInput.values()[keyNumber].bitMask) > 0u
    }

    operator fun set(key: KeyboardInput, isActive: Boolean) {
        chip8InputState = if (isActive) {
            chip8InputState or key.bitMask
        } else {
            chip8InputState and key.bitMask.inv()
        }

        LOG.debug { "Chip 8 key $key: $isActive. New input state: $chip8InputState" }

        observers.forEach {
            val keyEvent = InputEvent(key, isActive)
            it.receiveEvent(keyEvent)

            LOG.debug { "Updated observer: $it with key event: $keyEvent" }
        }
    }

    fun subscribe(inputObserver: InputObserver) {
        observers.add(inputObserver)

        LOG.info { "Added new observer: $inputObserver to Input Manager" }
    }

    companion object {
        private val LOG = logger(this::class)
    }
}
