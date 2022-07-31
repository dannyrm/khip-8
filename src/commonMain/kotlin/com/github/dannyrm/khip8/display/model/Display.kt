package com.github.dannyrm.khip8.display.model

import com.github.dannyrm.khip8.event.RomStateEvent
import com.github.dannyrm.khip8.event.RomStateObserver
import com.github.dannyrm.khip8.lineSeparator
import org.koin.core.annotation.Single

@Single
class Display(private val displayMemory: DisplayMemory): RomStateObserver {

    operator fun set(x: Int, y: Int, value: UByte) {
        displayMemory[x,y] = value
    }

    fun clear() = displayMemory.clear()

    fun hasCollision(): Boolean {
        if (displayMemory.collision) {
            displayMemory.collision = false
            return true
        }

        return false
    }

    // Clear the display memory whenever the rom state changes
    override fun receiveEvent(romStateEvent: RomStateEvent) {
        clear()
    }

    override fun toString(): String = "Display Memory {${lineSeparator()}$displayMemory}"
}