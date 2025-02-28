package com.abedatasolutions.ereports.core.models.test


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TestDetails(
    @SerialName("Id")
    val id: Int = 0,
    @SerialName("Branch")
    val branch: Int = 0,
    @SerialName("Code")
    val code: String = "",
    @SerialName("Name")
    val name: String = "",
    @SerialName("Description")
    val description: String = "",
    @SerialName("Specimen")
    val specimen: String = "",
    @SerialName("Preparation")
    val preparation: String = "",
    @SerialName("Running")
    val running: String = "",
    @SerialName("TAT")
    val tAT: String = ""
)