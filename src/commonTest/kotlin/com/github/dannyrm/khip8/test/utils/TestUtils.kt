package com.github.dannyrm.khip8.test.utils

import com.github.dannyrm.khip8.multiplatform.FileAbsolutePath

fun convertNumericParams(source: String, format: String? = null): IntArray {
    val sourceSplit = source.split(Regex(","))
    // Parse the format string - or if it's null default to "h" (hex)
    val formatSplit = format?.toCharArray()?.map { it.toString() } ?: "h".repeat(sourceSplit.size).toCharArray().map { it.toString() }

    val sourceAndFormatMapping = sourceSplit.zip(formatSplit)

    return sourceAndFormatMapping.map {
            if (it.second == "h") {
                it.first.toInt(16)
            } else {
                it.first.toInt(10)
            }
        }.toIntArray()
}

expect fun getAbsolutePath(fileName: String): String
expect fun loadFileAsByteArray(fileName: String): ByteArray
expect fun loadFileAsList(fileName: String): List<String>

expect fun createTempFile(filePrefix: String, fileSuffix: String): FileAbsolutePath

expect fun readFileAsString(filePath: String): String

expect fun deleteFile(filePath: String): Boolean

expect fun createFile(filePath: String): FileAbsolutePath

expect fun isFile(filePath: String): Boolean
expect fun isDirectory(filePath: String): Boolean

expect fun getFileName(filePath: String): String

operator fun IntArray.component6() = this[5]
operator fun IntArray.component7() = this[6]
