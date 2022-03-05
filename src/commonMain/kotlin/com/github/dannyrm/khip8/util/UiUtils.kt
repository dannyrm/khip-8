package com.github.dannyrm.khip8.util

import kotlin.math.floor

fun calculatePixelSize(dimensions: IntArray, windowWidth: Int, windowHeight: Int): Pair<Int, Int> {
    val (displayMemoryWidth, displayMemoryHeight) = dimensions

    val adjustedXPixelSize = floor(windowWidth.toDouble()/ displayMemoryWidth).toInt()
    val adjustedYPixelSize = floor(windowHeight.toDouble() / displayMemoryHeight).toInt()

    return Pair(adjustedXPixelSize, adjustedYPixelSize)
}