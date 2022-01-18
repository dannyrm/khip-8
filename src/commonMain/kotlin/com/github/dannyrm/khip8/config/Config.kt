package com.github.dannyrm.khip8.config

import com.github.dannyrm.khip8.util.SystemMode

data class Config(val systemSpeedConfig: SystemSpeedConfig,
                  val soundConfig: SoundConfig,
                  val memoryConfig: MemoryConfig,
                  val frontEndConfig: FrontEndConfig,
                  val systemMode: SystemMode
)

expect fun loadConfig(): Config

expect fun delayBetweenCycles(config: Config): Pair<Long, Int>