package com.github.dannyrm.khip8.display.view

import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import com.soywiz.klock.Frequency
import com.soywiz.korge.Korge
import com.soywiz.korge.view.*
import com.soywiz.korim.bitmap.Bitmap
import com.soywiz.korim.bitmap.Bitmap1
import com.soywiz.korim.color.Colors
import com.soywiz.korio.async.runBlockingNoJs

class KorgeUi: Ui {
    private lateinit var canvas: Bitmap1
    private lateinit var config: Config


    override fun init(config: Config, onCloseSignal: () -> Unit) {
        this.config = config
    }

    override fun update(displayMemory: DisplayMemory) {
        for (x in 0 until canvas.width) {
            for (y in 0 until canvas.height) {
                canvas[x, y] = 1
            }
        }
    }

    override fun halt() {
    }

    private suspend fun createUi(displayRefreshRate: Int) = Korge( width = 512, height = 256, title = "Khip-8", bgcolor = Colors["#2b2b2b"]) {
        addFixedUpdater(Frequency(displayRefreshRate.toDouble())) {
            canvas = Bitmap1(512, 256)
        }
    }
}
