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

    const val TESTS_BASE_URL = "https://www.abclab.com"
    private const val TESTS = "$TESTS_BASE_URL/eReportApple/Tests"
    const val GET_TESTS_VERSION = "$TESTS/GetVersion"
    const val GET_TEST = "$TESTS/GetList"
    const val GET_TEST_DETAILS = "$TESTS/GetDetails"
}