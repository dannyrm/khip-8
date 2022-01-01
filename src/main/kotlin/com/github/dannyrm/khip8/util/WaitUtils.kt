package com.github.dannyrm.khip8.util

fun waitFor(delayInMillis: Long, delayInNanos: Int) {
    Thread.sleep(delayInMillis, delayInNanos)
}