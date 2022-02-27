package com.github.dannyrm.khip8.display.view

import com.github.dannyrm.khip8.Khip8
import com.github.dannyrm.khip8.config.Config
import com.github.dannyrm.khip8.display.model.DisplayMemory
import kotlinx.coroutines.Job
import kotlin.math.floor

interface Ui {
    suspend fun start(config: Config, rootJob: Job, khip8: Khip8)

    fun calculatePixelSize(displayMemory: DisplayMemory, windowWidth: Int, windowHeight: Int): Pair<Int, Int> {
        val (displayMemoryWidth, displayMemoryHeight) = displayMemory.dimensions()

        val adjustedXPixelSize = floor(windowWidth.toDouble()/ displayMemoryWidth).toInt()
        val adjustedYPixelSize = floor(windowHeight.toDouble() / displayMemoryHeight).toInt()

        return Pair(adjustedXPixelSize, adjustedYPixelSize)
    }
}