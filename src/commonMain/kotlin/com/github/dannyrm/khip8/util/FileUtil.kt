package com.github.dannyrm.khip8.util

import com.soywiz.klogger.Logger
import kotlin.reflect.KClass

expect fun currentDirectory(): String

expect fun loadFile(filePath: String): ByteArray

expect fun saveToDisk(filePath: String, toSave: String)

expect fun constructDateTimeFileName(fileName: String): String

expect fun saveContentToDisk(toSave: String, fileName: String, fileExtension: String,
                             directory: String = currentDirectory(),
                             fileNameSuffixFunction: (p: String) -> String = ::constructDateTimeFileName)

expect fun memoryDump(toSave: String)

expect fun lineSeparator(): String

fun logger(klass: KClass<*>) = Logger(klass.qualifiedName ?: "Unknown")