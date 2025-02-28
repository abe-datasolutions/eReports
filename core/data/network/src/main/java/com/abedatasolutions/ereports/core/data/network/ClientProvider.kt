package com.abedatasolutions.ereports.core.data.network

import com.abedatasolutions.ereports.core.common.DebugMode
import com.abedatasolutions.ereports.core.models.serialization.Serialization
import io.ktor.client.HttpClient
import io.ktor.client.engine.android.Android
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.client.plugins.cookies.HttpCookies
import io.ktor.client.plugins.defaultRequest
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json

internal class ClientProvider(
    private val cookiesStorage: CookiesStorage = AcceptAllCookiesStorage(),
    private val baseUrl: BaseUrl = BaseUrl.current,
    private val debugMode: DebugMode = DebugMode.current
) {
    fun provideClient(): HttpClient = HttpClient(Android){
        followRedirects = false
        developmentMode = debugMode.value

        install(ContentNegotiation){
            json(Serialization.json)
        }
        install(HttpCookies){
            storage = cookiesStorage
        }
        install(
            CookieAuthenticationValidatorPlugin(cookiesStorage)
        )
        defaultRequest {
            url(baseUrl.value)
            contentType(ContentType.Application.Json)
        }
    }
}