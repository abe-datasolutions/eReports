package com.abedatasolutions.ereports.core.data.network.reports

import com.abedatasolutions.ereports.core.models.reports.FindReportsQuery
import com.abedatasolutions.ereports.core.models.reports.Report
import com.abedatasolutions.ereports.core.models.reports.ReportsQuery

interface ReportsApi {
    suspend fun getReports(query: ReportsQuery): List<Report>
    suspend fun findReports(query: FindReportsQuery): List<Report>
}