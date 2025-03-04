package com.abedatasolutions.ereports.core.data.network.patient

import com.abedatasolutions.ereports.core.common.logging.Logger
import com.abedatasolutions.ereports.core.data.network.Endpoints
import com.abedatasolutions.ereports.core.data.network.reports.ReportsQueries
import com.abedatasolutions.ereports.core.errors.network.AuthException
import com.abedatasolutions.ereports.core.models.patient.PatientInfo
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get

internal class PatientApiImpl(
    private val client: HttpClient
) : PatientApi {
    override suspend fun getPatientInfo(accession: String): PatientInfo? = try {
        client.get(Endpoints.GET_PATIENT_INFO){
            url {
                parameters.append(
                    ReportsQueries.PARAM_ACCESSION,
                    accession
                )
            }
        }.body()
    } catch (e: AuthException){
        throw e
    } catch (e: Exception){
        Logger.recordException(e)
        null
    }
}