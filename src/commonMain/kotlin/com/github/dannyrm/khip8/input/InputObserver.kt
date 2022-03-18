package com.github.dannyrm.khip8.input

interface InputObserver {
    fun receiveEvent(inputEvent: InputEvent)
}