package com.github.dannyrm.khip8.util

import com.github.dannyrm.khip8.display.model.DisplayMemory
import kotlin.math.floor

fun calculatePixelSize(displayMemory: DisplayMemory, windowWidth: Int, windowHeight: Int): Pair<Int, Int> {
    val (displayMemoryWidth, displayMemoryHeight) = displayMemory.dimensions()

    val adjustedXPixelSize = floor(windowWidth.toDouble()/ displayMemoryWidth).toInt()
    val adjustedYPixelSize = floor(windowHeight.toDouble() / displayMemoryHeight).toInt()

    return Pair(adjustedXPixelSize, adjustedYPixelSize)
}