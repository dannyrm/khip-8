package com.github.dannyrm.khip8.util

import java.io.File
import java.nio.file.Path
import java.nio.file.Paths
import java.time.Clock
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

fun saveToDisk(file: File, toSave: String): File {
    file.writeText(toSave)

    return file
}

fun constructDateTimeFileName(fileName: String): String = constructDateTimeFileName(fileName, Clock.systemDefaultZone())
fun constructDateTimeFileName(fileName: String, clock: Clock = Clock.systemDefaultZone()): String {
    return "$fileName-${DateTimeFormatter.ISO_DATE_TIME.format(LocalDateTime.now(clock)).replace(Regex(":|\\."), "-")}"
}

fun currentDirectory() = Paths.get("").toAbsolutePath()

fun saveContentToDisk(toSave: String, fileName: String, fileExtension: String, directory: Path = currentDirectory(),
                       fileNameSuffixFunction: (p: String) -> String = ::constructDateTimeFileName): File {
    return saveToDisk(
        directory.resolve(fileNameSuffixFunction(fileName)+"."+fileExtension).toFile(),
        toSave
    )
}

fun memoryDump(toSave: String): File {
    return saveContentToDisk(toSave, "memory-dump", "txt")
}