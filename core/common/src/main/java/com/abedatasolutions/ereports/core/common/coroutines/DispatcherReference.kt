package com.abedatasolutions.ereports.core.common.coroutines

/**
 * Represents different types of dispatchers.
 *
 * This enum class provides references to common dispatchers used for
 * coroutines and other asynchronous operations.
 *
 * Can provide [CoroutineDispatcher] through [DispatcherProvider]
 */
enum class DispatcherReference {
    Default,
    IO,
    Main,
}