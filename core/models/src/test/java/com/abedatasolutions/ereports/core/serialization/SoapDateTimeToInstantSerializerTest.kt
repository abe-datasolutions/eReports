package com.abedatasolutions.ereports.core.serialization

import assertk.assertThat
import assertk.assertions.contains
import assertk.assertions.isEqualTo
import com.abedatasolutions.ereports.core.models.reports.Report
import com.abedatasolutions.ereports.core.models.reports.ReportStatus
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.junit.Test

class SoapDateTimeToInstantSerializerTest {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Test
    fun testSerializeAndDeserializeOnString(){
        val epochMillis = 1691596800000L
        val stringSoapDateTime = "\"/Date($epochMillis)/\""
        val instant = json.decodeFromString(SoapDateTimeToInstantSerializer, stringSoapDateTime)

        assertThat(instant.toEpochMilliseconds()).isEqualTo(epochMillis)
        assertThat(
            json.encodeToString(SoapDateTimeToInstantSerializer, instant)
        ).isEqualTo(stringSoapDateTime)
    }

    @Test
    fun testDeserializeAndSerializeReport(){
        val body = """{"__type": "Support.ReportsMinimal","PatAcn": "230810001","PatName": "A CAHYAWATI","PatSex": "P","RptDt": "/Date(1691596800000)/","Status": "FINAL"}""".trimIndent()
        val expectedReport = Report(
            accession = "230810001",
            patientName = "A CAHYAWATI",
            gender = "P",
            reportDate = Instant.parse("2023-08-09T16:00:00Z"),
            status = ReportStatus.FINAL
        )
        val report = json.decodeFromString<Report>(body)

        assertThat(report).isEqualTo(expectedReport)
    }
}