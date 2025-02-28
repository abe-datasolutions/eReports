package com.abedatasolutions.ereports.core.models.patient


import com.abedatasolutions.ereports.core.common.datetime.LocalDatePattern.Companion.distantDate
import com.abedatasolutions.ereports.core.models.reports.ReportStatus
import com.abedatasolutions.ereports.core.models.serialization.StringDateTimeToInstantSerializer
import com.abedatasolutions.ereports.core.models.serialization.StringDateToLocalDateSerializer
import kotlinx.datetime.Clock
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.Padding
import kotlinx.datetime.format.char
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PatientInfo(
    @SerialName("Acn")
    val accession: String = "",
    @SerialName("MrNumber")
    val mrNumber: String = "",
    @SerialName("Address")
    val address: String = "",
    @SerialName("Name")
    val name: String = "",
    @SerialName("Csz")
    val csz: String = "",
    @SerialName("Phone")
    val phone: String = "",
    @SerialName("Dob")
    @Serializable(StringDateToLocalDateSerializer::class)
    val dateOfBirth: LocalDate? = null,
    @SerialName("Sex")
    val sex: String = "",
    @SerialName("PId")
    val pId: String = "",
    @SerialName("Phy")
    val phy: String = "",
    @SerialName("Age")
    val age: String = "",
    @SerialName("Demog")
    val demog: String = "",
    @SerialName("DtmCol")
    @Serializable(StringDateTimeToInstantSerializer::class)
    val dateCollected: Instant? = null,
    @SerialName("DtmRcv")
    @Serializable(StringDateTimeToInstantSerializer::class)
    val dateReceived: Instant? = null,
    @SerialName("DtmRpt")
    @Serializable(StringDateTimeToInstantSerializer::class)
    val dateReported: Instant? = null,
    @SerialName("ReportMsg")
    val reportMsg: String = "",
    @SerialName("Status")
    val status: ReportStatus = ReportStatus.FINAL,
    @SerialName("TestRecords")
    val testRecords: List<TestRecord> = emptyList(),
    @SerialName("Pathologist")
    val pathologist: String = "",
    @SerialName("MedTech")
    val medTech: String = "",
    @SerialName("ClientName")
    val clientName: String = "",
    @SerialName("ClientPhone")
    val clientPhone: String = "",
    @SerialName("ClientA1")
    val clientA1: String = "",
    @SerialName("ClientA2")
    val clientA2: String = "",
    @SerialName("ClientA3")
    val clientA3: String = "",
    @SerialName("ClientId")
    val clientId: String = ""
)