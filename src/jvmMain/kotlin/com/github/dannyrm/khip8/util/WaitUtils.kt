@file:JvmName("JvmWaitUtils")
package com.github.dannyrm.khip8.util

actual fun waitFor(delayInMillis: Long, delayInNanos: Int) {
    Thread.sleep(delayInMillis, delayInNanos)
}