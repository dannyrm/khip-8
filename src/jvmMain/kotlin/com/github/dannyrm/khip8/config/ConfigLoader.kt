package com.github.dannyrm.khip8.config

import com.sksamuel.hoplite.ConfigLoader
import java.math.BigDecimal
import java.math.RoundingMode
import kotlin.math.min

actual fun loadConfig(): Config = ConfigLoader().loadConfigOrThrow("/standard.json")

actual fun delayBetweenCycles(config: Config): Pair<Long, Int> {
    val cyclesDecimal = BigDecimal(1000.0 / config.systemSpeedConfig.cpuSpeed.toDouble())
    val numberOfMillis = cyclesDecimal.setScale(0, RoundingMode.DOWN)

    val fractionalValue = (cyclesDecimal - numberOfMillis)

    if (fractionalValue == BigDecimal.ZERO) {
        return Pair(numberOfMillis.toLong(), 0)
    }

    // Minus two to take into account the 0. at the beginning of the fraction
    val maxNumberOfValuesAfterPoint = fractionalValue.toString().length-2

    val numberOfNanos = fractionalValue.toString().replace("0.", "")
        .toBigInteger().toString()
        .substring(0..min(maxNumberOfValuesAfterPoint-1, 5)).toInt()

    return Pair(numberOfMillis.toLong(), numberOfNanos)
}