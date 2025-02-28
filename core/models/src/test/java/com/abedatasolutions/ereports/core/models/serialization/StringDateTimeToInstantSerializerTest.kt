package com.abedatasolutions.ereports.core.models.serialization

import assertk.assertThat
import assertk.assertions.isEqualTo
import assertk.assertions.isNotEqualTo
import com.abedatasolutions.ereports.core.common.datetime.InstantPattern
import com.abedatasolutions.ereports.core.common.datetime.LocalDatePattern
import com.abedatasolutions.ereports.core.common.datetime.LocalDateTimePattern
import com.abedatasolutions.ereports.core.models.reports.Report
import com.abedatasolutions.ereports.core.models.reports.ReportStatus
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.json.Json
import org.junit.Test

class StringDateTimeToInstantSerializerTest {
    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    @Test
    fun testSerializeAndDeserializeOnSoapInstantString(){
        val epochMillis = 1691596800000L
        val stringSoapDateTime = "\"/Date($epochMillis)/\""
        val trimmedSoapDateTime = stringSoapDateTime.trim('"')

        assertThat(
            InstantPattern.entries.find {
                it.pattern.toRegex().matches(trimmedSoapDateTime)
            }
        ).isEqualTo(InstantPattern.Soap)

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
    fun testSerializeAndDeserializeOnSoapDateTimeString(){
        val dateString = """"10/08/2023 07:25""""

        assertThat(
            LocalDateTimePattern.find(dateString.trim('"'))
        ).isEqualTo(LocalDateTimePattern.Soap)

        val instant = json.decodeFromString(StringDateTimeToInstantSerializer, dateString)
        val expectedInstant = LocalDateTime(
            year = 2023,
            monthNumber = 10,
            dayOfMonth = 8,
            hour = 7,
            minute = 25
        ).toInstant(TimeZone.currentSystemDefault())

        assertThat(instant).isEqualTo(expectedInstant)

        val serializedInstant = json.encodeToString(StringDateTimeToInstantSerializer, instant)

        assertThat(serializedInstant).isNotEqualTo(dateString)

        val deserializedInstant = json.decodeFromString(StringDateTimeToInstantSerializer, serializedInstant)

        assertThat(deserializedInstant).isEqualTo(instant)
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