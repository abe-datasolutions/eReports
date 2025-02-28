package com.abedatasolutions.ereports.core.data.network.patient

import com.abedatasolutions.ereports.core.models.patient.PatientInfo

interface PatientApi {
    suspend fun getPatientInfo(accession: String): PatientInfo
}