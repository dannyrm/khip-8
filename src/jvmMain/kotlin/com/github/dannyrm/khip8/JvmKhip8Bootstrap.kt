package com.github.dannyrm.khip8

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.JvmPropertiesSettings
import com.russhwolf.settings.Settings
import com.soywiz.korinject.injector
import org.koin.core.annotation.ComponentScan
import org.koin.java.KoinJavaComponent.inject
import java.io.ByteArrayInputStream
import java.util.*

@org.koin.core.annotation.Module
@ComponentScan
object JvmKhip8Bootstrap {
    @JvmStatic
    fun main(args: Array<String>) {
        Khip8Bootstrap.boot(listOf())
    }
}

actual fun lineSeparator() = System.lineSeparator()

@OptIn(ExperimentalSettingsImplementation::class)
actual fun loadProperties(propertiesInputStream: ByteArrayInputStream?): Settings? {
    return propertiesInputStream?.let {
        val properties = Properties()
        properties.load(propertiesInputStream)

        JvmPropertiesSettings(properties)
    }
}
