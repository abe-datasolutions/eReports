package com.abedatasolutions.ereports.core.errors.network

import com.abedatasolutions.ereports.core.errors.ApplicationException

sealed class NetworkException(message: String): ApplicationException(message){
    data class UnknownResponseException(val response: Any): NetworkException("Unknown Response: $response")
}