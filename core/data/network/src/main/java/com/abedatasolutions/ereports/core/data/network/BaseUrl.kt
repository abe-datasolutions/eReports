package com.abedatasolutions.ereports.core.data.network

/**
 * Represents the base URL for API requests.
 *
 * This class is a value class that holds the base URL string and provides
 * functionality to manage a singleton instance of the base URL. It also provides
 * a convenient extension property to append relative paths to the base URL.
 *
 * Base Url can be found in the `build-logic` configured under `Server Flavors`
 *
 * @property value The base URL string.
 * @constructor Creates a new `BaseUrl` instance and sets it as the singleton instance.
 *
 * @throws IllegalStateException if attempting to access `current` before initialization.
 */
@JvmInline
value class BaseUrl(val value: String) {
    init {
        setInstance(this)
    }

    companion object {
        @Volatile
        private var INSTANCE: BaseUrl? = null
        val current: BaseUrl
            get() = INSTANCE ?: error("BaseUrl is not initialized")

        val String.withBaseUrl: String
            get() = current.value.trimEnd('/') + this

        private fun setInstance(url: BaseUrl) {
            synchronized(this) {
                if (INSTANCE != null) return
                INSTANCE = url
            }
        }
    }
}