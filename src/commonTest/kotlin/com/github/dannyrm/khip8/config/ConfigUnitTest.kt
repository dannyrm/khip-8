package com.github.dannyrm.khip8.config

import com.github.dannyrm.khip8.util.SystemMode
import kotlin.test.Test
import kotlin.test.expect

class ConfigUnitTest {

    @Test
    fun `Check default config values used when settings is null`() {
        val config = DefaultConfig

        expect(540) { config.speedConfig().cpuSpeed }
        expect(60) { config.speedConfig().timerSpeed }
        expect(30) { config.speedConfig().displayRefreshRate }

        expect(4096) { config.memoryConfig().memorySize }
        expect(16) { config.memoryConfig().numberOfGeneralPurposeRegisters }
        expect(16) { config.memoryConfig().stackSize }
        expect(0) { config.memoryConfig().interpreterStartAddress }
        expect(512) { config.memoryConfig().programStartAddress }

        expect(512) { config.frontEndConfig().windowWidth }
        expect(256) { config.frontEndConfig().windowHeight }

        expect(2000.0) { config.soundConfig().toneFrequency }

        expect(SystemMode.SUPER_CHIP_MODE) { config.systemMode() }
    }
}