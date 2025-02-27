package com.abedatasolutions.ereports.core.models.patient


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestGroup(
    @SerialName("Group")
    val group: String? = null,
    @SerialName("Records")
    val records: List<Record> = emptyList()
)