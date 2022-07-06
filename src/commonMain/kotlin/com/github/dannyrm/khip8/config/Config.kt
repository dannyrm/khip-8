package com.github.dannyrm.khip8.config

import com.github.dannyrm.khip8.config.Khip8Property.*
import com.github.dannyrm.khip8.util.SystemMode
import com.russhwolf.settings.Settings
import kotlin.math.roundToLong

data class Config(val systemSpeedConfig: SystemSpeedConfig, val memoryConfig: MemoryConfig,
                  val frontEndConfig: FrontEndConfig = FrontEndConfig(),
                  val soundConfig: SoundConfig = SoundConfig(),
                  val systemMode: SystemMode = SystemMode.SUPER_CHIP_MODE)

fun buildConfig(settings: Settings?): Config {
    fun getSettingValue(property: Khip8Property) = settings?.getInt(property.propertyName, property.defaultValue) ?: property.defaultValue

    return Config(
        SystemSpeedConfig(
            cpuSpeed = getSettingValue(SPEED_CPU),
            timerSpeed = getSettingValue(SPEED_TIMER),
            displayRefreshRate = getSettingValue(SPEED_DISPLAY_REFRESH)
        ),
        MemoryConfig(
            memorySize = getSettingValue(MEMORY_RAM_SIZE),
            stackSize = getSettingValue(MEMORY_STACK_SIZE),
            interpreterStartAddress = getSettingValue(MEMORY_INTERPRETER_START_ADDRESS),
            programStartAddress = getSettingValue(MEMORY_PROGRAM_START_ADDRESS),
            numberOfGeneralPurposeRegisters = getSettingValue(MEMORY_PROGRAM_REGISTER_COUNT)
        )
    )
}

fun delayBetweenCycles(config: Config) = (1000.0 / config.systemSpeedConfig.cpuSpeed.toDouble()).roundToLong()
fun numberOfCpuTicksPerPeripheralTick(config: Config): Int = config.systemSpeedConfig.cpuSpeed / config.systemSpeedConfig.timerSpeed

private enum class Khip8Property(val propertyName: String, val defaultValue: Int) {
    SPEED_CPU("system.speed.cpu", 540),
    SPEED_TIMER("system.speed.timer", 60),
    SPEED_DISPLAY_REFRESH("system.speed.display.refresh", 30),

    MEMORY_RAM_SIZE("system.memory.ram.size", 4096),
    MEMORY_STACK_SIZE("system.memory.stack.size", 16),
    MEMORY_INTERPRETER_START_ADDRESS("system.memory.interpreter.start.address", 0),
    MEMORY_PROGRAM_START_ADDRESS("system.memory.program.start.address", 512),
    MEMORY_PROGRAM_REGISTER_COUNT("system.memory.register.count", 16), // 16 registers, named Vx where x = 1...F
}
