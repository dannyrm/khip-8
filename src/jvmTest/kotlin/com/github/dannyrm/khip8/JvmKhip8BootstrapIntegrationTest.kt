package com.github.dannyrm.khip8

import com.github.dannyrm.khip8.TestFileUtils.loadResource
import io.mockk.mockk
import io.mockk.verify
import org.junit.jupiter.api.Test
import org.koin.dsl.module
import org.koin.test.KoinTest
import java.io.File

class JvmKhip8BootstrapIntegrationTest: KoinTest {

    @Test
    fun `Check app starts up correctly`() {
        val khip8 = mockk<Khip8>(relaxed = true)

        val rom = File(loadResource("inputs/test-roms/c8_test.ch8"))

        JvmKhip8Bootstrap.boot(rom.absolutePath,
            listOf(module {
                single { khip8 }
            })
        )

        verify { khip8.load(rom.absolutePath) }
        verify { khip8.start() }
    }
}