package com.github.dannyrm.khip8

data class Khip8Status(var khip8State: Khip8State, var loadedRom: ByteArray? = null)

enum class Khip8State {
    STOPPED, RUNNING, PAUSED
}