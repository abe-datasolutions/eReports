package com.abedatasolutions.ereports.core.common.logging

class CombinedLogger(
    private vararg val loggers: Logger
): Logger {
    override fun recordException(t: Throwable) {
        loggers.forEach {
            it.recordException(t)
        }
    }

    override fun log(message: String) {
        loggers.forEach {
            it.log(message)
        }
    }

    override fun setCustomKey(key: String, value: String) {
        loggers.forEach {
            it.setCustomKey(key, value)
        }
    }
}