package com.github.dannyrm.khip8

data class Khip8Status(var khip8State: Khip8State, var loadedRomPath: String? = null)

enum class Khip8State {
    EMPTY, LOADED
}