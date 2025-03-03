package com.abedatasolutions.ereports.core.data.network.reports

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isFailure
import assertk.assertions.isNotNull
import assertk.assertions.isSuccess
import com.abedatasolutions.ereports.core.common.DebugMode
import com.abedatasolutions.ereports.core.common.test.MainDispatcherRule
import com.abedatasolutions.ereports.core.data.network.BaseUrl
import com.abedatasolutions.ereports.core.data.network.ClientProvider
import com.abedatasolutions.ereports.core.data.network.authenticate
import com.abedatasolutions.ereports.core.errors.network.AuthException
import com.abedatasolutions.ereports.core.models.file.FileType
import com.abedatasolutions.ereports.core.models.reports.FindReportsQuery
import com.abedatasolutions.ereports.core.models.reports.QueryGender
import com.abedatasolutions.ereports.core.models.reports.ReportStatus
import com.abedatasolutions.ereports.core.models.reports.ReportsQuery
import io.ktor.client.HttpClient
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import io.ktor.client.plugins.cookies.CookiesStorage
import kotlinx.coroutines.async
import kotlinx.coroutines.awaitAll
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class ReportsApiImplTest {
    @get:Rule
    val rule = MainDispatcherRule()
    private lateinit var api: ReportsApi
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
        api = ReportsApiImpl(client, rule.testDispatcher)
    }

    @After
    fun tearDown() {
        client.close()
    }

    @Test
    fun endpointsThrowExceptionIfUnauthenticated() = runTest {
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
    fun testGetReports() = runTest {
        authenticate(client)
        val finalsResult = runCatching {
            api.getReports(
                query = ReportsQuery(
                    startRowIndex = 0,
                    maxRows = 100,
                    status = ReportStatus.FINAL
                )
            )
        }

        assertThat(finalsResult).isSuccess()

        finalsResult.getOrDefault(emptyList()).also {
            println("Reports: ${it.size}")
        }.forEach {
            println(it)
            assertThat(it.status).isEqualTo(ReportStatus.FINAL)
        }

        val partialsResult = runCatching {
            api.getReports(
                query = ReportsQuery(
                    startRowIndex = 0,
                    maxRows = 100,
                    status = ReportStatus.PARTIAL
                )
            )
        }

        assertThat(partialsResult).isSuccess()

        partialsResult.getOrDefault(emptyList()).also {
            println("Reports: ${it.size}")
        }.forEach {
            println(it)
            assertThat(it.status).isEqualTo(ReportStatus.PARTIAL)
        }
    }

    @Test
    fun testFindReports() = runTest {
        authenticate(client)
        val defaultQueryResult = runCatching {
            api.findReports(
                FindReportsQuery(
                    startRowIndex = 0,
                    maxRows = 100,
                    accession = null,
                    patientName = null,
                    gender = QueryGender.F,
                    dateFrom = null,
                    dateTo = null,
                    sortColumn = null
                )
            )
        }

        assertThat(defaultQueryResult).isSuccess()

        defaultQueryResult.getOrDefault(emptyList()).also {
            println("Reports: ${it.size}")
        }.forEach {
            println(it)
        }
    }

    @Test
    fun testCreatePdfFromReports() = runTest {
        authenticate(client)
        val reports = api.getReports(
            query = ReportsQuery(
                startRowIndex = 0,
                maxRows = 100,
                status = ReportStatus.FINAL
            )
        )

        reports.shuffled().take(10).map {
            async {
                val result = runCatching {
                    api.createPdf(it.accession)
                }

                assertThat(result).isSuccess()

                val file = result.getOrNull()

                println(file?.byteArray?.size)
                assertThat(file).isNotNull()
                assertThat(file?.type).isEqualTo(FileType.PDF)
            }
        }.awaitAll()
    }
}