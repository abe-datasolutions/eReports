package com.abedatasolutions.ereports.core.models.reports

import kotlinx.datetime.LocalDate

data class FindReportsQuery(
    val startRowIndex: Int = 0,
    val maxRows: Int = MAXIMUM_ROWS,
    val accession: String? = null,
    val patientName: String? = null,
    val gender: QueryGender? = null,
    val dateFrom: LocalDate? = null,
    val dateTo: LocalDate? = null,
    val sortColumn: SortColumn? = null,
){

    companion object {
        const val MAXIMUM_ROWS = ReportsQuery.MAXIMUM_ROWS
    }
}
