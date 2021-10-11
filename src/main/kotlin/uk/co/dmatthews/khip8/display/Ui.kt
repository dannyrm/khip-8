package uk.co.dmatthews.khip8.display

interface Ui {
    fun init(onCloseSignal: () -> Unit)
    fun update(displayMemory: DisplayMemory)
    fun halt()
}