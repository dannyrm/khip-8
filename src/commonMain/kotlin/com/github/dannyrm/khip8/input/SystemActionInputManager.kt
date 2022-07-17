package com.github.dannyrm.khip8.input

import org.koin.core.annotation.Single

@Single
class SystemActionInputManager() {
    var memoryDumpFunction: () -> Unit = {}
}
