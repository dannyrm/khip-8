package com.github.dannyrm.khip8

object Khip8Status {
    var loadedRom: ByteArray? = null
}

enum class RunningState {
    STOPPED, RUNNING, PAUSED
}