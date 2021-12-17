package uk.co.dmatthews.khip8.display.model

import uk.co.dmatthews.khip8.display.view.Ui

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
        return "Display Memory {${System.lineSeparator()}$displayMemory}"
    }
}