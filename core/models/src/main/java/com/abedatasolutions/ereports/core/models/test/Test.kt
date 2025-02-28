package com.abedatasolutions.ereports.core.models.test


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Test(
    @SerialName("Id")
    val id: Int = 0,
    @SerialName("Branch")
    val branch: Int = 0,
    @SerialName("Code")
    val code: String = "",
    @SerialName("Name")
    val name: String = ""
)