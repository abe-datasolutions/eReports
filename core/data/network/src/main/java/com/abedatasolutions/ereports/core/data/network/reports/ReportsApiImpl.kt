package com.abedatasolutions.ereports.core.data.network.reports

import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.Endpoints
import com.abedatasolutions.ereports.core.data.network.reports.ReportsQueries.params
import com.abedatasolutions.ereports.core.errors.network.AuthException
import com.abedatasolutions.ereports.core.models.file.ByteArrayFile
import com.abedatasolutions.ereports.core.models.file.FileType
import com.abedatasolutions.ereports.core.models.reports.FindReportsQuery
import com.abedatasolutions.ereports.core.models.reports.Report
import com.abedatasolutions.ereports.core.models.reports.ReportsQuery
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
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
            val response = client.get(Endpoints.CREATE_PDF){
                url {
                    parameters.append(ReportsQueries.PARAM_ACCESSION, accession)
                }
            }
            val contentType = response.contentType() ?: error("Unknown File Type")
            val fileType = contentType.let { type ->
                FileType.entries.find {
                    it.mimeType == "${type.contentType}/${type.contentSubtype}"
                }
            } ?: error("Unknown FileType: $contentType")
            val byteArray = response.body<ByteArray>()

            ByteArrayFile(byteArray, fileType)
        } catch (e: AuthException){
            throw e
        } catch (e: Exception){
            Logger.recordException(e)
            null
        }
    }
}