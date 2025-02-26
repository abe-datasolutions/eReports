package com.abedatasolutions.ereports.core.models.auth


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginData(
    @SerialName("uId")
    val userId: String = "",
    @SerialName("pwd")
    val password: String = ""
)