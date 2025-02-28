package com.abedatasolutions.ereports.core.data.network

import com.abedatasolutions.ereports.core.common.logging.Logger
import io.ktor.client.plugins.api.ClientPlugin
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.encodedPath

internal class CookieAuthenticationValidatorPlugin(
    storage: CookiesStorage
): ClientPlugin<Unit> by createClientPlugin(
    name = "CookieAuthenticationValidator",
    body = {
        onRequest { request, _ ->
            val cookies = storage.get(request.url.build())
            val isAuthenticated = cookies.find {
                it.name == ".ASPXAUTH"
            } != null
            Logger.Debug.log("Request Path: ${request.url.encodedPath}")
            Logger.Debug.log("Cookies: $cookies")
            Logger.Debug.log("Authenticated: $isAuthenticated")
        }
    }
)