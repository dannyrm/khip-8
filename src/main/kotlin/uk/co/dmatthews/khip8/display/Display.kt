package uk.co.dmatthews.khip8.display

class Display(private val displayMemory: DisplayMemory,
              private val ui: Ui) {

    operator fun set(x: Int, y: Int, value: UByte) {
        displayMemory[x,y] = value
    }

    fun init(onCloseSignal: () -> Unit) {
        ui.init(onCloseSignal)
    }

    fun tick() {
        ui.update(displayMemory)
    }

    fun clear() {
        displayMemory.clear()
    }

    override fun toString(): String {
        return "Display Memory {${System.lineSeparator()}$displayMemory}"
    }
}