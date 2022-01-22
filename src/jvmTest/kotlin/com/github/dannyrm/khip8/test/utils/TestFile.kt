package com.github.dannyrm.khip8.test.utils

import java.io.File

actual class TestFile actual constructor(fileName: String, fromClasspath: Boolean, tempFile: Boolean) {
    private val file: File

    init {
        val loadFunction: (String) -> File = if (fromClasspath) {
            ::loadResource
        } else {
            ::loadFile
        }

        file = if (tempFile) {
            File.createTempFile(fileName, "khip8TestFileSuffix")
        } else {
            loadFunction(fileName)
        }
    }

    actual fun getAbsolutePath(): String = file.absolutePath
    actual fun getFileName(): String = file.name

    actual fun asString(): String = file.readText()
    actual fun asByteArray(): ByteArray = file.readBytes()
    actual fun asStringList(): List<String> = file.readLines()

    actual fun delete(): Boolean = file.delete()
    actual fun isFile(): Boolean = file.isFile
    actual fun isDirectory(): Boolean = file.isDirectory
}

private fun loadFile(fileName: String): File = File(fileName)
private fun loadResource(name: String): File = File(object {}.javaClass.classLoader.getResource(name).file)