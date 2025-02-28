package com.abedatasolutions.ereports.core.common

/**
 * Represents the debug mode of the application.
 *
 * This class provides a thread-safe, globally accessible way to determine if the application
 * is running in debug mode. It utilizes a singleton pattern implemented with a `value class`
 * and a `companion object` to ensure a single instance is shared throughout the application.
 *
 * @property value A boolean representing whether debug mode is enabled (true) or disabled (false).
 *   Defaults to `false`.
 *
 * Example Usage:
 * ```kotlin
 * if (DebugMode.current.value) {
 *     // Perform debug-specific actions
 *     println("Debug mode is enabled!")
 * }
 *
 * // To enable debug mode:
 * val debugOn = DebugMode(true)
 *
 * // To disable debug mode or reset to default
 * val debugOff = DebugMode(false)
 *
 * //Check the current state of debug
 * val isDebug = DebugMode.current.value
 * ```
 */
@JvmInline
value class DebugMode(val value: Boolean = false){
    init {
        setInstance(this)
    }

    companion object {
        @Volatile
        private var INSTANCE: DebugMode = DebugMode()
        val current: DebugMode
            get() = INSTANCE

        val isDebug: Boolean
            get() = INSTANCE.value

        private fun setInstance(mode: DebugMode) {
            synchronized(this) {
                if (INSTANCE == mode) return
                INSTANCE = mode
            }
        }
    }
}
