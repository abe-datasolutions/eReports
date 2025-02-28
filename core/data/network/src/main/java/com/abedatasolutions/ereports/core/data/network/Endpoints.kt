package com.abedatasolutions.ereports.core.data.network

object Endpoints {
    private const val AUTH = "/WSCommon.asmx"
    const val LOGIN = "$AUTH/UsersCheckLogin2"
    const val IS_AUTHENTICATED = "$AUTH/UsersIsAuthenticated"

    private const val PATIENT = "/PatientService.asmx"
    const val GET_PATIENT_INFO = "$PATIENT/GetPatientInfoDto"

    private const val RESULTS = "/ResultsService.asmx"
    const val FIND_REPORTS = "$RESULTS/FindReports"
    const val FIND_REPORTS_FILTERED = "$RESULTS/FindReportsFiltered"
    const val CREATE_PDF = "/createpdf.aspx"
}