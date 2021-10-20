package uk.co.dmatthews.khip8.util

fun waitFor(delayInMillis: Long, delayInNanos: Int) {
    Thread.sleep(delayInMillis, delayInNanos)
}