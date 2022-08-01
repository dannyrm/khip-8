package com.github.dannyrm.khip8.observers

import com.github.dannyrm.khip8.observers.events.InputEvent

interface InputObserver {
    fun receiveEvent(inputEvent: InputEvent)
}