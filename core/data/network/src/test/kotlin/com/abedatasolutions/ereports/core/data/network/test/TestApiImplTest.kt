package com.abedatasolutions.ereports.core.data.network.test

import com.abedatasolutions.ereports.core.common.DebugMode
import com.abedatasolutions.ereports.core.data.network.BaseUrl
import com.abedatasolutions.ereports.core.data.network.ClientProvider
import io.ktor.client.HttpClient
import kotlinx.coroutines.test.runTest
import org.junit.After
import org.junit.Before
import org.junit.Test

class TestApiImplTest{
    private lateinit var client: HttpClient
    private lateinit var api: TestApi

    @Before
    fun setup(){
        val provider = ClientProvider(
            baseUrl = BaseUrl(""),
            debugMode = DebugMode(true)
        )
        client = provider.provideTestClient()
        api = TestApiImpl(client)
    }

    @After
    fun teardown(){
        client.close()
    }

    @Test
    fun testFetchingVersion() = runTest {
        val version = api.getVersion()
        println("Version: $version")
    }

    @Test
    fun testFetchingList() = runTest {
        val tests = api.getList()
        println("Tests: ${tests.size}")
        tests.forEach {
            println(it)
        }
    }
}