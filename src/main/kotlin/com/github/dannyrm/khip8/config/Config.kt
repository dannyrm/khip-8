package com.github.dannyrm.khip8.config

import com.github.dannyrm.khip8.util.SystemMode

data class Config(val systemSpeedConfig: SystemSpeedConfig,
                  val soundConfig: SoundConfig,
                  val memoryConfig: MemoryConfig,
                  val systemMode: SystemMode
)