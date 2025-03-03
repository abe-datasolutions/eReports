package com.abedatasolutions.ereports.core.data.network.patient

import assertk.assertThat
import assertk.assertions.isNotNull
import com.abedatasolutions.ereports.core.common.DebugMode
import com.abedatasolutions.ereports.core.common.test.MainDispatcherRule
import com.abedatasolutions.ereports.core.data.network.BaseUrl
import com.abedatasolutions.ereports.core.data.network.ClientProvider
import com.abedatasolutions.ereports.core.data.network.authenticate
import com.abedatasolutions.ereports.core.data.network.reports.ReportsApi
import com.abedatasolutions.ereports.core.data.network.reports.ReportsApiImpl
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

class PatientApiImplTest {
    @get:Rule
    val rule = MainDispatcherRule()
    private lateinit var reportsApi: ReportsApi
    private lateinit var patientApi: PatientApi
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
        reportsApi = ReportsApiImpl(client, rule.testDispatcher)
        patientApi = PatientApiImpl(client)
    }

    @After
    fun tearDown() {
        client.close()
    }

    @Test
    fun testGetPatientInfoFromReports() = runTest {
        authenticate(client)
        val reports = reportsApi.getReports(
            ReportsQuery(
                startRowIndex = 0,
                maxRows = 100,
                status = ReportStatus.FINAL
            )
        ).shuffled().take(10)

        reports.map {
            async {
                val patientInfo = patientApi.getPatientInfo(it.accession)

                println(patientInfo)
                assertThat(patientInfo).isNotNull()
            }
        }.awaitAll()
    }
}