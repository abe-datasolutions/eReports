package com.abedatasolutions.ereports.core.models.patient


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestRecord(
    @SerialName("Test")
    val test: String? = null,
    @SerialName("TestGroups")
    val testGroups: List<TestGroup> = emptyList()
)