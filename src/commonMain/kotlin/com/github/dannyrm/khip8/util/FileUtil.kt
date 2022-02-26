package com.github.dannyrm.khip8.util

import com.soywiz.klogger.Logger
import com.soywiz.klogger.setLevel
import kotlin.reflect.KClass

typealias FileAbsolutePath = String

expect fun currentDirectory(): FileAbsolutePath

expect fun loadFile(filePath: String): ByteArray

expect fun saveToDisk(filePath: String, toSave: String): FileAbsolutePath

expect fun constructDateTimeFileName(fileName: String): FileAbsolutePath

expect fun saveContentToDisk(toSave: String, fileName: String, fileExtension: String,
                             directory: String = currentDirectory(),
                             fileNameSuffixFunction: (p: String) -> String = ::constructDateTimeFileName): FileAbsolutePath

expect fun memoryDump(toSave: String): FileAbsolutePath

expect fun lineSeparator(): FileAbsolutePath

fun logger(klass: KClass<*>) = Logger(klass.qualifiedName ?: "Unknown").setLevel(Logger.Level.INFO)
