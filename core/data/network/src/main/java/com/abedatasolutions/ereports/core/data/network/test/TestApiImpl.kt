package com.abedatasolutions.ereports.core.data.network.test

import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.Endpoints
import com.abedatasolutions.ereports.core.data.network.reports.ReportsQueries
import com.abedatasolutions.ereports.core.models.test.Test
import com.abedatasolutions.ereports.core.models.test.TestDetails
import com.abedatasolutions.ereports.core.models.test.TestDetailsQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

class TestApiImpl(
    private val client: HttpClient
) : TestApi {
    override suspend fun getVersion(branch: Int): Int = try {
        client.get(Endpoints.GET_TESTS_VERSION){
            url {
                parameters.append(
                    PARAM_BRANCH,
                    branch.toString()
                )
            }
        }.body<String>().toIntOrNull() ?: -1
    }catch (e: Exception){
        Logger.recordException(e)
        -1
    }

    override suspend fun getList(branch: Int): List<Test> {
        return client.get(Endpoints.GET_TESTS_VERSION){
            url {
                parameters.append(
                    PARAM_BRANCH,
                    branch.toString()
                )
            }
        }.body()
    }

    override suspend fun getDetails(query: TestDetailsQuery): TestDetails? = try {
        client.get(Endpoints.GET_TESTS_VERSION){
            url {
                parameters.append(
                    PARAM_BRANCH,
                    query.branch.toString()
                )
                parameters.append(
                    PARAM_TEST_CODE,
                    query.testCode
                )
            }
        }.body()
    }catch (e: Exception){
        Logger.recordException(e)
        null
    }

    companion object{
        private const val PARAM_BRANCH = "branch"
        private const val PARAM_TEST_CODE = "testCode"
    }
}