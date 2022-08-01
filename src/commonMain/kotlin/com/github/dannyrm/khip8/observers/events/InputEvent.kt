package com.github.dannyrm.khip8.observers.events

import com.github.dannyrm.khip8.input.KeyboardInput

data class InputEvent(val keyboardInput: KeyboardInput, val isActive: Boolean)