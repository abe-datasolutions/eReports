package com.abedatasolutions.ereports.core.data.network

import assertk.assertThat
import assertk.assertions.isSuccess
import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.auth.AuthApiImpl
import com.abedatasolutions.ereports.core.models.auth.LoginData
import io.ktor.client.HttpClient

internal suspend fun authenticate(client: HttpClient){
        val api = AuthApiImpl(client)
        val loginData = LoginData(
            userId = System.getenv("VALID_USER"),
            password = System.getenv("VALID_PASSWORD")
        ).also {
            Logger.log(it.toString())
        }
        assertThat(
            runCatching {
                api.login(loginData)
            }
        ).isSuccess()
    }