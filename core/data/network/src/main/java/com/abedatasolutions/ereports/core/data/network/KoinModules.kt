package com.abedatasolutions.ereports.core.data.network

import com.abedatasolutions.ereports.core.data.network.auth.AuthApi
import com.abedatasolutions.ereports.core.data.network.auth.AuthApiImpl
import com.abedatasolutions.ereports.core.data.network.patient.PatientApi
import com.abedatasolutions.ereports.core.data.network.patient.PatientApiImpl
import com.abedatasolutions.ereports.core.data.network.reports.ReportsApi
import com.abedatasolutions.ereports.core.data.network.reports.ReportsApiImpl
import com.abedatasolutions.ereports.core.data.network.test.TestApi
import com.abedatasolutions.ereports.core.data.network.test.TestApiImpl
import io.ktor.client.plugins.cookies.AcceptAllCookiesStorage
import org.koin.core.qualifier.named
import org.koin.dsl.module

val httpClientModule = module {
    single {
        ClientProvider(
            cookiesStorage = AcceptAllCookiesStorage(), //TODO: Custom Implementation
        )
    }
    single(qualifier = named(ClientProvider.TESTS_HTTPCLIENT_MARKER)){
        get<ClientProvider>().provideTestClient()
    }
    single{
        get<ClientProvider>().provideClient()
    }
}

val apiModule = module {
    includes(httpClientModule)
    single<TestApi> {
        TestApiImpl(
            get(qualifier = named(ClientProvider.TESTS_HTTPCLIENT_MARKER))
        )
    }
    single<AuthApi> {
        AuthApiImpl(
            get()
        )
    }
    single<ReportsApi> {
        ReportsApiImpl(
            get()
        )
    }
    single<PatientApi> {
        PatientApiImpl(
            get()
        )
    }
}