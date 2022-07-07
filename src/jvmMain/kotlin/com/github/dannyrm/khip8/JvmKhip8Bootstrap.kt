package com.github.dannyrm.khip8

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPropertiesSettings
import com.russhwolf.settings.Settings
import java.io.ByteArrayInputStream
import java.util.*

object JvmKhip8Bootstrap {

    @JvmStatic
    fun main(args: Array<String>) {
        Khip8Bootstrap.boot(listOf())
    }
}

actual fun lineSeparator() = System.lineSeparator()

@OptIn(ExperimentalSettingsImplementation::class)
actual fun loadProperties(propertiesInputStream: ByteArrayInputStream): Settings {
    val properties = Properties()
    properties.load(propertiesInputStream)

    return JvmPropertiesSettings(properties)
}