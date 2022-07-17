package com.github.dannyrm.khip8.config

import com.github.dannyrm.khip8.loadProperties
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import kotlin.math.roundToLong

open class ConfigManager(configData: () -> ByteArrayInputStream?) {
    private val config: Config

    init {
        val settings = loadProperties(configData())

        fun getSettingValue(property: Khip8Property) = settings?.getInt(property.propertyName, property.defaultValue) ?: property.defaultValue

        config = Config(
            SystemSpeedConfig(
                cpuSpeed = getSettingValue(Khip8Property.SPEED_CPU),
                timerSpeed = getSettingValue(Khip8Property.SPEED_TIMER),
                displayRefreshRate = getSettingValue(Khip8Property.SPEED_DISPLAY_REFRESH)
            ),
            MemoryConfig(
                memorySize = getSettingValue(Khip8Property.MEMORY_RAM_SIZE),
                stackSize = getSettingValue(Khip8Property.MEMORY_STACK_SIZE),
                interpreterStartAddress = getSettingValue(Khip8Property.MEMORY_INTERPRETER_START_ADDRESS),
                programStartAddress = getSettingValue(Khip8Property.MEMORY_PROGRAM_START_ADDRESS),
                numberOfGeneralPurposeRegisters = getSettingValue(Khip8Property.MEMORY_PROGRAM_REGISTER_COUNT)
            )
        )
    }

    fun speedConfig() = config.systemSpeedConfig
    fun memoryConfig() = config.memoryConfig
    fun soundConfig() = config.soundConfig
    fun frontEndConfig() = config.frontEndConfig
    fun systemMode() = config.systemMode

    fun delayBetweenCycles(): Long = (1000.0 / speedConfig().cpuSpeed.toDouble()).roundToLong()
    fun numberOfCpuTicksPerPeripheralTick(): Int = speedConfig().cpuSpeed / speedConfig().timerSpeed
}

class Chip8PropertiesConfig: ConfigManager({
        runBlocking {
            ByteArrayInputStream(resourcesVfs["chip8.properties"].readAll())
        }
    }
)

object DefaultConfig: ConfigManager({ null })
