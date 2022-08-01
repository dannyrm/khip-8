package com.github.dannyrm.khip8.observers

data class RomStateEvent(val rom: ByteArray?, val status: RomStatus)

enum class RomStatus {
    LOADED,
    UNLOADED
}