package com.github.dannyrm.khip8.test.utils

import com.github.dannyrm.khip8.multiplatform.FileAbsolutePath

// Convert a set of comma separated String values into integers. By default, the function assumes the values are
// all hex unless a format is given which says otherwise.
fun convertNumericParams(source: String, format: String? = null): IntArray {

    fun createFormat(source: List<String>, format: String?): List<String> {
        return format?.toCharArray()?.map {it.toString() } ?: CharArray(source.size) { 'h' }.map { it.toString() }
    }

    val sourceSplit = source.split(Regex(","))

    return sourceSplit.zip(createFormat(sourceSplit, format)).map {
            if (it.second == "h") {
                it.first.toInt(16)
            } else {
                it.first.toInt(10)
            }
        }.toIntArray()
}

operator fun IntArray.component6() = this[5]
operator fun IntArray.component7() = this[6]
