package com.github.dannyrm.khip8.display.view

import com.github.dannyrm.khip8.display.model.DisplayMemory

interface Ui {
    fun init(onCloseSignal: () -> Unit)
    fun update(displayMemory: DisplayMemory)
    fun halt()
}