package com.github.dannyrm.khip8.config

import com.github.dannyrm.khip8.util.SystemMode
import com.russhwolf.settings.Settings
import kotlin.math.roundToLong

data class Config(val systemSpeedConfig: SystemSpeedConfig = SystemSpeedConfig(),
                  val memoryConfig: MemoryConfig = MemoryConfig(),
                  val frontEndConfig: FrontEndConfig = FrontEndConfig(),
                  val soundConfig: SoundConfig = SoundConfig(),
                  val systemMode: SystemMode = SystemMode.SUPER_CHIP_MODE
)

fun buildConfig(settings: Settings): Config {
    val config = Config()

    settings.getIntOrNull(SYSTEM_SPEED_CPU_PROPERTY_NAME)?.let {
        config.systemSpeedConfig.cpuSpeed = it
    }

    return config
}

fun delayBetweenCycles(config: Config) = (1000.0 / config.systemSpeedConfig.cpuSpeed.toDouble()).roundToLong()
fun numberOfCpuTicksPerPeripheralTick(config: Config): Int = config.systemSpeedConfig.cpuSpeed / config.systemSpeedConfig.timerSpeed

const val SYSTEM_SPEED_CPU_PROPERTY_NAME = "system.speed.cpu"