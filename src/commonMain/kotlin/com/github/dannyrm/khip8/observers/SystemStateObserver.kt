package com.github.dannyrm.khip8.observers

import com.github.dannyrm.khip8.RunningState

interface SystemStateObserver {
    fun receiveEvent(runningState: RunningState)
}