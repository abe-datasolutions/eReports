package com.abedatasolutions.ereports.core.data.network.auth

import assertk.assertThat
import assertk.assertions.isEmpty
import assertk.assertions.isFailure
import assertk.assertions.isFalse
import assertk.assertions.isNotEmpty
import assertk.assertions.isSuccess
import assertk.assertions.isTrue
import com.abedatasolutions.ereports.core.common.DebugMode
import com.abedatasolutions.ereports.core.data.network.BaseUrl
import com.abedatasolutions.ereports.core.data.network.ClientProvider
import com.abedatasolutions.ereports.core.errors.network.AuthException
import com.abedatasolutions.ereports.core.models.auth.LoginData
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import io.ktor.http.Url
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class AuthApiImplTest {
    private lateinit var client: HttpClient
    private lateinit var api: AuthApi
    private lateinit var cookiesStorage: CookiesStorage
    private val baseUrl: BaseUrl = BaseUrl("https://jkt-dev-inetrep.abclab.com.ph")

    @Before
    fun setup(){
        cookiesStorage = AcceptAllCookiesStorage()
        val provider = ClientProvider(
            cookiesStorage = cookiesStorage,
            baseUrl = baseUrl,
            debugMode = DebugMode(true)
        )
        client = provider.provideClient()
        api = AuthApiImpl(client)
    }

    @After
    fun tearDown() {
        client.close()
    }

    @Test
    fun testLogin() = runTest {
        assertThat(cookiesStorage.get(Url(baseUrl.value))).isEmpty()

        val result = runCatching {
            api.login(
                LoginData(
                    userId = "ABCJKT",
                    password = "ABCJKT"
                )
            )
        }

        assertThat(result).isSuccess()

        val cookies = cookiesStorage.get(Url(baseUrl.value))

        assertThat(cookies).isNotEmpty()

        cookies.forEach {
            println(it)
        }
    }

    @Test
    fun invalidLoginCredentialsThrowsException() = runTest {
        val result = runCatching {
            api.login(
                LoginData(
                    userId = "",
                    password = ""
                )
            )
        }

        assertThat(result).isFailure()
        assertThat(result.exceptionOrNull() is AuthException.InvalidCredentialsException).isTrue()
    }

    @Test
    fun invalidExpiredCredentialsThrowsException() = runTest {
        val result = runCatching {
            api.login(
                LoginData(
                    userId = "ALFDCJKT",
                    password = "ALFDC"
                )
            )
        }

        assertThat(result).isFailure()
        assertThat(result.exceptionOrNull() is AuthException.AccountExpiredException).isTrue()
    }

    @Test
    fun testIsAuthenticated() = runTest {
        assertThat(cookiesStorage.get(Url(baseUrl.value))).isEmpty()
        assertThat(api.isAuthenticated()).isFalse()

        val result = runCatching {
            api.login(
                LoginData(
                    userId = "ABCJKT",
                    password = "ABCJKT"
                )
            )
        }

        assertThat(result).isSuccess()
        assertThat(api.isAuthenticated()).isTrue()
    }
}