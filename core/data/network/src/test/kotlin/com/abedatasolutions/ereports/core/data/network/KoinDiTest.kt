package com.abedatasolutions.ereports.core.data.network

import assertk.assertThat
import assertk.assertions.isNotEqualTo
import io.ktor.client.HttpClient
import io.ktor.client.request.get
import io.ktor.client.statement.request
import kotlinx.coroutines.test.runTest
import org.junit.Test
import org.koin.core.context.startKoin
import org.koin.core.context.stopKoin
import org.koin.core.qualifier.named
import org.koin.test.KoinTest
import org.koin.test.get

class KoinDiTest: KoinTest {
    @Test
    fun koinProvidesHttpClientForMainAndTests() = runTest {
        BaseUrl(SAMPLE_BASE_URL)
        startKoin {
            modules(httpClientModule)
        }
        val mainHttpClient = get<HttpClient>()
        val testsHttpClient = get<HttpClient>(
            qualifier = named(ClientProvider.TESTS_HTTPCLIENT_MARKER)
        )

        println("Main: $mainHttpClient")
        println("Tests: $testsHttpClient")

        assertThat(mainHttpClient).isNotEqualTo(testsHttpClient)

        val mainUrl = mainHttpClient.get(Endpoints.IS_AUTHENTICATED).request.url
        val testUrl = testsHttpClient.get {  }.request.url

        println(mainUrl)
        println(testUrl)
        assertThat(mainUrl).isNotEqualTo(testUrl)
        stopKoin()
    }

    companion object{
        private const val SAMPLE_BASE_URL = "https://jkt-dev-inetrep.abclab.com.ph"
    }
}