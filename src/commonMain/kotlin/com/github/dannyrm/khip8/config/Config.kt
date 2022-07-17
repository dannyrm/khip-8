package com.github.dannyrm.khip8.config

import com.github.dannyrm.khip8.util.SystemMode

data class Config(val systemSpeedConfig: SystemSpeedConfig, val memoryConfig: MemoryConfig,
                  val frontEndConfig: FrontEndConfig = FrontEndConfig(),
                  val soundConfig: SoundConfig = SoundConfig(),
                  val systemMode: SystemMode = SystemMode.SUPER_CHIP_MODE)