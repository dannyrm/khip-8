package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.config.Config
import com.sksamuel.hoplite.ConfigLoader
import org.junit.jupiter.api.Test
import strikt.api.expectThat
import strikt.assertions.isEqualTo
import com.github.dannyrm.khip8.util.SystemMode

class ConfigIntegrationTest {

    @Test
    fun `Check Config loads as expected`() {
        val config = ConfigLoader().loadConfigOrThrow<Config>("/standard.json")

        expectThat(config.soundConfig.midiNoteNumber).isEqualTo(80)
        expectThat(config.soundConfig.midiInstrumentNumber).isEqualTo(0)
        expectThat(config.soundConfig.midiNoteVelocity).isEqualTo(100)

        expectThat(config.systemSpeedConfig.cpuSpeed).isEqualTo(540)
        expectThat(config.systemSpeedConfig.timerSpeed).isEqualTo(60)
        expectThat(config.systemSpeedConfig.displayRefreshRate).isEqualTo(30)

        expectThat(config.systemMode).isEqualTo(SystemMode.SUPER_CHIP_MODE)

        expectThat(config.memoryConfig.programStartAddress).isEqualTo(0x200)
        expectThat(config.memoryConfig.interpreterStartAddress).isEqualTo(0x0)
        expectThat(config.memoryConfig.memorySize).isEqualTo(4096)
        expectThat(config.memoryConfig.stackSize).isEqualTo(16)
    }
}