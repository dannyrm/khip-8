package uk.co.dmatthews.khip8

import org.junit.jupiter.params.converter.SimpleArgumentConverter

class HexToIntegerCsvSourceArgumentConverter: SimpleArgumentConverter() {

    override fun convert(source: Any, targetType: Class<*>): Any {
        val value = source as String
        return value.toInt(16)
    }
}