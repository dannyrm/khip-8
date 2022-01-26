@file:JvmName("JvmFileUtil")
package com.github.dannyrm.khip8.util

import java.io.File
import java.nio.file.Paths
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

actual fun saveToDisk(filePath: String, toSave: String): FileAbsolutePath {
    File(filePath).writeText(toSave)

    return filePath
}

actual fun loadFile(filePath: String): ByteArray = File(filePath).readBytes()

actual fun constructDateTimeFileName(fileName: String): String = constructDateTimeFileName(fileName, Clock.systemDefaultZone())

fun constructDateTimeFileName(fileName: String, clock: Clock = Clock.systemDefaultZone()): FileAbsolutePath {
    return "$fileName-${DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now(clock)).replace(Regex(":|\\."), "-")}"
}

actual fun currentDirectory(): FileAbsolutePath = Paths.get("").toAbsolutePath().toString()

actual fun saveContentToDisk(toSave: String, fileName: String, fileExtension: String, directory: String, fileNameSuffixFunction: (p: String) -> String): FileAbsolutePath {
    return saveToDisk(
        Paths.get(directory, fileNameSuffixFunction(fileName)+"."+fileExtension).toString(),
        toSave
    )
}

actual fun memoryDump(toSave: String): FileAbsolutePath {
    return saveContentToDisk(toSave, "memory-dump", "txt")
}

actual fun lineSeparator() = System.lineSeparator()
