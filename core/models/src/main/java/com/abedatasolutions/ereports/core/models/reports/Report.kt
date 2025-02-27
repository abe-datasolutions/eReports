package com.abedatasolutions.ereports.core.models.reports


import com.abedatasolutions.ereports.core.models.serialization.StringDateTimeToInstantSerializer
import kotlinx.datetime.Instant
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Report(
    @SerialName("PatAcn")
    val accession: String = "",
    @SerialName("PatName")
    val patientName: String = "",
    @SerialName("PatSex")
    val gender: String = "",
    @SerialName("RptDt")
    @Serializable(StringDateTimeToInstantSerializer::class)
    val reportDate: Instant,
    @SerialName("Status")
    val status: ReportStatus = ReportStatus.FINAL
)