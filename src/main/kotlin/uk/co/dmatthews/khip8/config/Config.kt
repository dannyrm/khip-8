package uk.co.dmatthews.khip8.config

import uk.co.dmatthews.khip8.util.SystemMode

data class Config(val systemSpeedConfig: SystemSpeedConfig,
                  val soundConfig: SoundConfig,
                  val memoryConfig: MemoryConfig,
                  val systemMode: SystemMode)