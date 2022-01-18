package com.github.dannyrm.khip8.config

import com.github.dannyrm.khip8.util.SystemMode
import kotlin.test.Test
import kotlin.test.expect

class ConfigIntegrationTest {

    @Test
    fun `Check Config loads as expected`() {
        val config = loadConfig()

        expect(80) { config.soundConfig.midiNoteNumber }
        expect(0) { config.soundConfig.midiInstrumentNumber }
        expect(100) { config.soundConfig.midiNoteVelocity }

        expect(540) { config.systemSpeedConfig.cpuSpeed }
        expect(60) { config.systemSpeedConfig.timerSpeed }
        expect(30) { config.systemSpeedConfig.displayRefreshRate }

        expect(SystemMode.SUPER_CHIP_MODE) { config.systemMode }

        expect(0x200) { config.memoryConfig.programStartAddress }
        expect(0x0) { config.memoryConfig.interpreterStartAddress }
        expect(4096) { config.memoryConfig.memorySize }
        expect(16) { config.memoryConfig.stackSize }

        expect(FrontEndType.JAVA_AWT) { config.frontEndConfig.frontEnd }
    }
}