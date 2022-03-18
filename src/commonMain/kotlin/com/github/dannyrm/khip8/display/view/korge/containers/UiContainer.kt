package com.github.dannyrm.khip8.display.view.korge.containers

import com.github.dannyrm.khip8.Khip8
import com.soywiz.korge.input.onClick
import com.soywiz.korge.ui.*
import com.soywiz.korge.view.*
import com.soywiz.korio.file.VfsFile
import com.soywiz.korio.file.baseName

fun Container.khip8UiContainer(khip8: Khip8, romList: List<VfsFile>): KorgeUiContainer = KorgeUiContainer(khip8, romList).addTo(this)

class KorgeUiContainer(private val khip8: Khip8, private val romList: List<VfsFile>): Container() {
    var selectedRom: String? = null
    var pauseButtonText: String = "Pause"

    init {
        renderUi()
    }

    fun renderUi() {
        uiHorizontalStack(height = TOP_UI_HEIGHT) {
            uiButton(text = pauseButtonText)
                .onClick {
                    pauseButtonText = if (khip8.togglePause()) PAUSE_TEXT else UNPAUSE_TEXT
                }

            uiButton(text = "Reset")
                .onClick { khip8.reset() }

            uiPropertyComboBox(title = "",  field = ::selectedRom, values = romList.map { it.baseName }, width = 200.0)

            uiButton(text = "Load")
                .onClick {
                    khip8.load(findSelectedFile())
                }
        }
    }

    private suspend fun findSelectedFile() = romList.find { it.baseName == selectedRom }?.readBytes()

    companion object {
        const val TOP_UI_HEIGHT = 30.0
        const val PAUSE_TEXT = "Pause"
        const val UNPAUSE_TEXT = "Unpause"
    }
}
