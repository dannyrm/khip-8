package com.github.dannyrm.khip8.display.view.korge.containers

import com.github.dannyrm.khip8.Khip8
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.uiButton
import com.soywiz.korge.ui.uiHorizontalStack
import com.soywiz.korge.view.Container
import com.soywiz.korge.view.addTo
import com.soywiz.korge.view.position

fun Container.khip8UiContainer(khip8: Khip8): KorgeUiContainer = KorgeUiContainer(khip8).addTo(this)

class KorgeUiContainer(khip8: Khip8): Container() {

    init {
        uiHorizontalStack(height = TOP_UI_HEIGHT) {
            uiButton(text = "Reset")
                .position(0, 0)
                .onClick { khip8.reset() }
        }
    }

    companion object {
        const val TOP_UI_HEIGHT = 30.0
    }
}