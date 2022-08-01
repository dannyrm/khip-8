package com.github.dannyrm.khip8.observers

interface RomStateObserver {
    fun receiveEvent(romStateEvent: RomStateEvent)
}
