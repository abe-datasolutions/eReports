package com.abedatasolutions.ereports.core.serialization

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import assertk.assertions.isTrue
import com.abedatasolutions.ereports.core.common.datetime.InstantPattern
import com.abedatasolutions.ereports.core.models.reports.Report
import com.abedatasolutions.ereports.core.models.reports.ReportStatus
import com.abedatasolutions.ereports.core.models.serialization.StringDateTimeToInstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import org.junit.Test

class StringDateTimeToInstantSerializerTest {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Test
    fun testSerializeAndDeserializeOnString(){
        val epochMillis = 1691596800000L
        val stringSoapDateTime = "\"/Date($epochMillis)/\""

//        assertThat(
//            InstantPattern.entries.find {
//                it.pattern.toRegex().matches(stringSoapDateTime)
//            }
//        ).isEqualTo(InstantPattern.Soap)
        val instant = json.decodeFromString(StringDateTimeToInstantSerializer, stringSoapDateTime)

        assertThat(instant.toEpochMilliseconds()).isEqualTo(epochMillis)

        val encodedInstant = json.encodeToString(StringDateTimeToInstantSerializer, instant)

//        assertThat(
//            InstantPattern.entries.find {
//                it.pattern.toRegex().matches(encodedInstant)
//            }
//        ).isEqualTo(InstantPattern.ISO)
        assertThat(encodedInstant).isNotEqualTo(stringSoapDateTime)
        assertThat(
            json.decodeFromString(StringDateTimeToInstantSerializer, encodedInstant)
        ).isEqualTo(instant)
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