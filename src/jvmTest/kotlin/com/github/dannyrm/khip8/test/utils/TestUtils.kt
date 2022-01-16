@file:JvmName("JvmTestUtils")
package com.github.dannyrm.khip8.test.utils

import com.github.dannyrm.khip8.multiplatform.FileAbsolutePath
import java.io.File

actual fun getAbsolutePath(fileName: String): String = loadFile(fileName).absolutePath
actual fun loadFileAsByteArray(fileName: String): ByteArray = loadFile(fileName).readBytes()
actual fun loadFileAsList(fileName: String): List<String> = loadFile(fileName).readLines()

actual fun createTempFile(filePrefix: String, fileSuffix: String): FileAbsolutePath = File.createTempFile(filePrefix, fileSuffix).absolutePath

actual fun readFileAsString(filePath: String): String = File(filePath).readText()

actual fun deleteFile(filePath: String): Boolean = File(filePath).delete()

actual fun createFile(filePath: String): FileAbsolutePath = File(filePath).absolutePath

actual fun isFile(filePath: String): Boolean = File(filePath).isFile
actual fun isDirectory(filePath: String): Boolean = File(filePath).isDirectory

actual fun getFileName(filePath: String): String = File(filePath).name

fun loadFile(fileName: String): File = File(loadResource(fileName))
fun loadResource(name: String) = object {}.javaClass.classLoader.getResource(name).file
