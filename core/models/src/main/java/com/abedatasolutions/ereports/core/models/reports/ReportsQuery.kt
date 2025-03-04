package com.abedatasolutions.ereports.core.models.reports

data class ReportsQuery(
    val startRowIndex: Int = 0,
    val maxRows: Int = MAXIMUM_ROWS,
    val status: ReportStatus = ReportStatus.FINAL
){

    companion object {
        const val MAXIMUM_ROWS = 30
    }
}
