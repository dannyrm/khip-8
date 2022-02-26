package com.github.dannyrm.khip8.display.view

import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory

interface Ui {
    fun init(config: Config, onCloseSignal: () -> Unit)
    fun update(displayMemory: DisplayMemory)
    fun halt()
}