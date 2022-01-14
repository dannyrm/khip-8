package com.github.dannyrm.khip8.util

fun convertNumericParams(source: String, format: String? = null): IntArray {
    // Remove the single quotes and split into tokens
    val sourceSplit = source.replace("'", "").split(Regex(","))
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