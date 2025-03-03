package com.abedatasolutions.ereports.core.data.network

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNotEqualTo
import assertk.assertions.isSuccess
import com.abedatasolutions.ereports.core.common.DebugMode
import com.abedatasolutions.ereports.core.common.test.MainDispatcherRule
import com.abedatasolutions.ereports.core.data.network.auth.AuthApiImpl
import com.abedatasolutions.ereports.core.data.network.patient.PatientApiImpl
import com.abedatasolutions.ereports.core.data.network.reports.ReportsApiImpl
import com.abedatasolutions.ereports.core.errors.network.AuthException
import com.abedatasolutions.ereports.core.models.auth.LoginData
import com.abedatasolutions.ereports.core.models.reports.FindReportsQuery
import com.abedatasolutions.ereports.core.models.reports.ReportsQuery
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class CookieAuthenticationValidatorPluginTest {
    @get:Rule
    val rule = MainDispatcherRule()
    private lateinit var client: HttpClient
    private lateinit var cookiesStorage: CookiesStorage
    private val baseUrl: BaseUrl = BaseUrl("https://jkt-dev-inetrep.abclab.com.ph")


    @Before
    fun setUp() {
        cookiesStorage = AcceptAllCookiesStorage()
        val provider = ClientProvider(
            cookiesStorage = cookiesStorage,
            baseUrl = baseUrl,
            debugMode = DebugMode(true)
        )
        client = provider.provideClient()
    }

    @After
    fun tearDown() {
        client.close()
    }

    @Test
    fun cookieIsNotValidatedOnAuthEndpoints() = runTest {
        val api = AuthApiImpl(client)
        val loginData = LoginData(
            "ABCJKT",
            "ABCJKT",
        )
        val loginException = runCatching {
            api.login(loginData)
        }.exceptionOrNull()

        assertThat(loginException).isNotEqualTo(AuthException.UnauthorizedException)
        assertThat(loginException).isNotEqualTo(AuthException.SessionExpiredException)

        val isAuthenticatedException = runCatching {
            api.isAuthenticated()
        }.exceptionOrNull()

        assertThat(isAuthenticatedException).isNotEqualTo(AuthException.UnauthorizedException)
        assertThat(isAuthenticatedException).isNotEqualTo(AuthException.SessionExpiredException)
    }

    @Test
    fun reportsApiEndpointsReturnExceptionIfNotAuthenticated() = runTest {
        val api = ReportsApiImpl(client, rule.testDispatcher)

        val getReportsResult = runCatching {
            api.getReports(ReportsQuery())
        }
        assertThat(getReportsResult).isFailure()
        assertThat(getReportsResult.exceptionOrNull()).isEqualTo(AuthException.UnauthorizedException)

        val findReportsResult = runCatching {
            api.findReports(FindReportsQuery())
        }
        assertThat(findReportsResult).isFailure()
        assertThat(findReportsResult.exceptionOrNull()).isEqualTo(AuthException.UnauthorizedException)

        val createPdfResult = runCatching {
            api.createPdf("")
        }
        assertThat(createPdfResult).isFailure()
        assertThat(createPdfResult.exceptionOrNull()).isEqualTo(AuthException.UnauthorizedException)

        authenticate(client)

        assertThat(
            runCatching {
                api.getReports(ReportsQuery())
            }
        ).isSuccess()
        assertThat(
            runCatching {
                api.findReports(FindReportsQuery())
            }
        ).isSuccess()
        assertThat(
            runCatching {
                api.createPdf("")
            }
        ).isSuccess()
    }

    @Test
    fun patientEndpointsReturnExceptionIfNotAuthenticated() = runTest {
        val api = PatientApiImpl(client)

        val getPatientResult = runCatching {
            api.getPatientInfo("")
        }
        assertThat(getPatientResult).isFailure()
        assertThat(getPatientResult.exceptionOrNull()).isEqualTo(AuthException.UnauthorizedException)

        authenticate(client)

        assertThat(
            runCatching {
                api.getPatientInfo("")
            }
        ).isSuccess()
    }
}