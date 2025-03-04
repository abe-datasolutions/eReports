package com.abedatasolutions.ereports.core.data.network

import com.abedatasolutions.ereports.core.common.logging.Logger
import io.ktor.client.call.HttpClientCall
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.request.HttpRequest
import kotlin.time.Duration.Companion.milliseconds

internal object HttpCallLogger {

    private fun log(call: HttpClientCall){
        with(call){
            logRequest(request)
            val requestDuration = (response.responseTime.timestamp - response.requestTime.timestamp).milliseconds
            Logger.log("Request Duration: $requestDuration")
        }
    }

    fun logRequest(request: HttpRequest, cause: Throwable? = null){
        val requestPath = request.url.encodedPath
        Logger.log("Request Path: $requestPath")
        Logger.log("Request Query: ${request.url.encodedQuery}")
        cause?.let {
            Logger.log("Request Exception: ${it::class.simpleName}")
            Logger.recordException(it)
        }
    }

    object Plugin: ClientPlugin<Unit> by createClientPlugin(
        name = "HttpCallLogger",
        body = {
            onResponse { response ->
                log(response.call)
            }
        }
    )
}