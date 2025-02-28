package com.abedatasolutions.ereports.core.common.logging

import java.time.Instant

object JvmLogger: Logger {
    private val logs = mutableListOf<String>()
    private val pairs = mutableMapOf<String, String>()
    override fun recordException(t: Throwable) = synchronized(this) {
        logs.forEach { message ->
            println(message)
        }
        logs.clear()
        pairs.forEach { (key, value) ->
            println(
                buildString {
                    append(key)
                    append(' ')
                    append(value)
                }
            )
        }
        pairs.clear()
        t.printStackTrace()
    }

    override fun log(message: String) {
        synchronized(this) {
            logs.add(
                buildString {
                    append(Instant.now())
                    append(' ')
                    append(message)
                }
            )
        }
        println(message)
    }

    override fun setCustomKey(key: String, value: String) {
        synchronized(this) {
            pairs[key] = value
        }
        println("$key: $value")
    }
}