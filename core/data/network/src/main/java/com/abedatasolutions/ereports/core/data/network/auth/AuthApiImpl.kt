package com.abedatasolutions.ereports.core.data.network.auth

import com.abedatasolutions.ereports.core.data.network.Endpoints
import com.abedatasolutions.ereports.core.errors.network.AuthException
import com.abedatasolutions.ereports.core.models.auth.LoginData
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.client.request.setBody

internal class AuthApiImpl(
    private val client: HttpClient
) : AuthApi {
    override suspend fun login(data: LoginData) {
        val result = client.post(Endpoints.LOGIN){
            setBody(data)
        }.body<Int>()

        when(result){
            0 -> throw AuthException.InvalidCredentialsException
            1 -> return
            2 -> throw AuthException.AccountExpiredException
            else -> error("Unknown Response: $result")
        }
    }

    override suspend fun isAuthenticated(): Boolean {
        return client.post(Endpoints.IS_AUTHENTICATED).body()
    }
}