package com.github.dannyrm.khip8.display.view

import com.github.dannyrm.khip8.config.Config
import kotlinx.coroutines.Job

interface Ui {
    suspend fun start(config: Config)
}