package com.github.dannyrm.khip8.display.model

import com.github.dannyrm.khip8.lineSeparator

class Display(private val displayMemory: DisplayMemory) {

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

    override fun toString(): String = "Display Memory {${lineSeparator()}$displayMemory}"
}