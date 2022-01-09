package com.github.dannyrm.khip8

import org.junit.jupiter.params.converter.SimpleArgumentConverter

class HexToIntegerCsvSourceArgumentConverter: SimpleArgumentConverter() {

    override fun convert(source: Any, targetType: Class<*>): Any {
        return if (targetType.isAssignableFrom(IntArray::class.java)) {
            val value = source as String
            // Remove the single quotes and split into tokens - then convert into base 64 Ints
            value.replace("'", "")
                .split(Regex(","))
                .map { it.toInt(16) }.toIntArray()
        } else {
            val value = source as String
            value.toInt(16)
        }
    }
}