package com.abedatasolutions.ereports.core.data.network.auth

import com.abedatasolutions.ereports.core.models.auth.LoginData

interface AuthApi {
    suspend fun login(data: LoginData)
    suspend fun isAuthenticated(): Boolean
}