package com.abedatasolutions.ereports.core.data.network.test

import com.abedatasolutions.ereports.core.models.test.Test
import com.abedatasolutions.ereports.core.models.test.TestDetails
import com.abedatasolutions.ereports.core.models.test.TestDetailsQuery

interface TestApi {
    suspend fun getVersion(branch: Int = 2): Int
    suspend fun getList(branch: Int = 2): List<Test>
    suspend fun getDetails(query: TestDetailsQuery): TestDetails?
}