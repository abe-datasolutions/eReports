package com.abedatasolutions.ereports.core.common.logging

/**
 * The Logger interface provides a centralized way to record exceptions, log messages, and set custom keys
 * for debugging and monitoring purposes. It is designed to be used for both fatal and non-fatal error
 * reporting, as well as for general logging.
 *
 * This interface allows for:
 *   - Recording non-fatal exceptions.
 *   - Logging messages to be included in reports.
 *   - Setting custom key-value pairs for context.
 *   - A singleton instance management for global logging.
 *
 *  The default implementation is `JvmLogger` but can be changed using the `setLogger` method.
 */
interface Logger {
    /**
     * Records a non-fatal report for Analysis.
     *
     * @param t a [Throwable] to be recorded as a non-fatal event.
     */
    fun recordException(t: Throwable)

    /**
     * Logs a message that's included in the next fatal, non-fatal, or ANR report.
     *
     * @param message the message to be logged
     */
    fun log(message: String)

    /**
     * Sets a custom key and value that are associated with subsequent fatal, non-fatal, and ANR
     * reports.
     *
     * Multiple calls to this method with the same key update the value for that key.
     *
     * The value of any key at the time of a fatal, non-fatal, or ANR event is associated with that
     * event.
     *
     * @param key A unique key
     * @param value A value to be associated with the given key
     */
    fun setCustomKey(key: String, value: String)

    companion object {
        var instance: Logger = JvmLogger
            private set

        fun setLogger(logger: Logger) = synchronized(this){
            instance = logger
        }
    }
}