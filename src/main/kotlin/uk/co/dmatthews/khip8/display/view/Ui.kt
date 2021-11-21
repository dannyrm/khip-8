package uk.co.dmatthews.khip8.display.view

import uk.co.dmatthews.khip8.display.model.DisplayMemory

interface Ui {
    fun init(onCloseSignal: () -> Unit)
    fun update(displayMemory: DisplayMemory)
    fun halt()
}
