package com.github.dannyrm.khip8.input.event

interface InputObserver {
    fun receiveEvent(inputEvent: InputEvent)
}