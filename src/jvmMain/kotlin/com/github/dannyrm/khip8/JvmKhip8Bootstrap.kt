package com.github.dannyrm.khip8

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPropertiesSettings
import com.soywiz.korio.file.std.resourcesVfs
import kotlinx.coroutines.runBlocking
import java.io.ByteArrayInputStream
import java.util.*

@OptIn(ExperimentalSettingsImplementation::class)
object JvmKhip8Bootstrap {

    @JvmStatic
    fun main(args: Array<String>) {
        Khip8Bootstrap.boot(
            JvmPropertiesSettings(loadProperties()), listOf()
        )
    }

    private fun loadProperties(): Properties {
        val propertiesFileInputStream = runBlocking {
            ByteArrayInputStream(resourcesVfs["chip8.properties"].readAll())
        }

        val properties = Properties()
        properties.load(propertiesFileInputStream)

        return properties
    }
}

actual fun lineSeparator() = System.lineSeparator()