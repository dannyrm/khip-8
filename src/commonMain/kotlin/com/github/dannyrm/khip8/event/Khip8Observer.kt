package com.github.dannyrm.khip8.event

interface Khip8Observer {
    fun receiveEvent(khip8Event: Khip8Event)
}