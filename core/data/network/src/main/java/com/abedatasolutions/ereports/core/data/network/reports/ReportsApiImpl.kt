package com.abedatasolutions.ereports.core.data.network.reports

import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.Endpoints
import com.abedatasolutions.ereports.core.data.network.reports.ReportsQueries.params
import com.abedatasolutions.ereports.core.models.file.ByteArrayFile
import com.abedatasolutions.ereports.core.models.file.FileType
import com.abedatasolutions.ereports.core.models.reports.FindReportsQuery
import com.abedatasolutions.ereports.core.models.reports.Report
import com.abedatasolutions.ereports.core.models.reports.ReportsQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.bodyAsChannel
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

internal class ReportsApiImpl(
    private val client: HttpClient,
    private val dispatcher: CoroutineDispatcher = Dispatchers.IO
) : ReportsApi {
    override suspend fun getReports(query: ReportsQuery): List<Report> {
        return client.get(Endpoints.FIND_REPORTS){
            url {
                parameters.appendAll(query.params)
            }
        }.body()
    }

    override suspend fun findReports(query: FindReportsQuery): List<Report> {
        return client.get(Endpoints.FIND_REPORTS_FILTERED){
            url {
                parameters.appendAll(query.params)
            }
        }.body()
    }

    override suspend fun createPdf(accession: String): ByteArrayFile? = withContext(dispatcher) {
        try {
            val response = client.get(Endpoints.FIND_REPORTS_FILTERED){
                url {
                    parameters.append(ReportsQueries.PARAM_ACCESSION, accession)
                }
            }

            val fileType = response.contentType()?.let { contentType ->
                FileType.entries.find {
                    it.mimeType == contentType.contentType
                }
            } ?: error("Unknown FileType")
            response.bodyAsChannel()
            val byteArray = response.body<ByteArray>()

            ByteArrayFile(byteArray, fileType)
        }catch (e: Exception){
            Logger.recordException(e)
            null
        }
    }
}