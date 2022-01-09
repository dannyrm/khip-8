package com.github.dannyrm.khip8.display.model

import com.github.dannyrm.khip8.display.view.Ui
import com.github.dannyrm.khip8.util.lineSeparator

class Display(private val displayMemory: DisplayMemory, private val ui: Ui) {

    operator fun set(x: Int, y: Int, value: UByte) {
        displayMemory[x,y] = value
    }

    fun tick() {
        ui.update(displayMemory)
    }

    fun clear() {
        displayMemory.clear()
    }

    // TODO: Write tests around collisions
    fun hasCollision(): Boolean {
        val hasCollision = displayMemory.collision
        displayMemory.collision = false

        return hasCollision
    }

    override fun toString(): String {
        return "Display Memory {${lineSeparator()}$displayMemory}"
    }
}