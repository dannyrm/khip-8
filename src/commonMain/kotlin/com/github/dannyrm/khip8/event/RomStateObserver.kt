package com.github.dannyrm.khip8.event

interface RomStateObserver {
    fun receiveEvent(romStateEvent: RomStateEvent)
}
