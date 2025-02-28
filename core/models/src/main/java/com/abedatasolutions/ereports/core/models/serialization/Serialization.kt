package com.abedatasolutions.ereports.core.models.serialization

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

object Serialization {
    val json by lazy {
        Json {
            ignoreUnknownKeys = true
            encodeDefaults = true
        }
    }

    inline fun <reified T: Any> T.encode(): String = json.encodeToString(this)
    inline fun <reified T: Any> String.decode(): T = json.decodeFromString(this)
}