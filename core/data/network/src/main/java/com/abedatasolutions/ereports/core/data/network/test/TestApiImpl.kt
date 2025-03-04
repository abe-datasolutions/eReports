package com.abedatasolutions.ereports.core.data.network.test

import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.Endpoints
import com.abedatasolutions.ereports.core.models.test.Test
import com.abedatasolutions.ereports.core.models.test.TestDetails
import com.abedatasolutions.ereports.core.models.test.TestDetailsQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.forms.submitForm
import io.ktor.client.statement.bodyAsText
import io.ktor.http.Parameters

internal class TestApiImpl(
    private val client: HttpClient
) : TestApi {
    override suspend fun getVersion(branch: Int): Int = try {
        val response = client.submitForm(
            Endpoints.GET_TESTS_VERSION,
            formParameters = Parameters.build {
                append(PARAM_BRANCH, branch.toString())
            }
        )

        Logger.Debug.setCustomKey("GetVersionResponse", response.bodyAsText())
        response.body<String>().replace("\"", "").toIntOrNull() ?: -1
    }catch (e: Exception){
        Logger.recordException(e)
        -1
    }

    override suspend fun getList(branch: Int): List<Test> {
        val response = client.submitForm(
            Endpoints.GET_TESTS,
            formParameters = Parameters.build {
                append(PARAM_BRANCH, branch.toString())
            }
        )

        Logger.Debug.setCustomKey("GetListResponse", response.bodyAsText())
        return response.body()
    }

    override suspend fun getDetails(query: TestDetailsQuery): TestDetails? = try {
        val response = client.submitForm(
            Endpoints.GET_TEST_DETAILS,
            formParameters = Parameters.build {
                append(PARAM_BRANCH, query.branch.toString())
                append(PARAM_TEST_CODE, query.testCode)
            }
        )

        Logger.Debug.setCustomKey("GetDetailsResponse", response.bodyAsText())
        response.body()
    }catch (e: Exception){
        Logger.recordException(e)
        null
    }

    companion object{
        private const val PARAM_BRANCH = "branch"
        private const val PARAM_TEST_CODE = "testCode"
    }
}