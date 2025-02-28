package com.abedatasolutions.ereports.core.data.network.reports

import com.abedatasolutions.ereports.core.common.datetime.LocalDatePattern
import com.abedatasolutions.ereports.core.models.reports.FindReportsQuery
import com.abedatasolutions.ereports.core.models.reports.ReportsQuery
import io.ktor.util.StringValues

internal object ReportsQueries {
    private const val PARAM_START_ROW_INDEX = "startRowIndex"
    private const val PARAM_MAX_ROWS = "maximumRows"
    private const val PARAM_REPORT_STATUS = "status"
    const val PARAM_ACCESSION = "acn"
    private const val PARAM_PATIENT_NAME = "patname"
    private const val PARAM_GENDER = "gender"
    private const val PARAM_DATE_FROM = "dtfrom"
    private const val PARAM_DATE_TO = "dtto"
    private const val PARAM_SORT_COLUMN = "sortColumn"

    /**
     * Represents a query for fetching reports.
     *
     * @property startRowIndex The index of the first row to retrieve.
     * @property maxRows The maximum number of rows to retrieve.
     * @property status The status of the reports to retrieve.
     */
    val ReportsQuery.params: StringValues
        get() = StringValues.build {
            append(PARAM_START_ROW_INDEX, startRowIndex.toString())
            append(PARAM_MAX_ROWS, maxRows.toString())
            append(PARAM_REPORT_STATUS, status.name)
        }

    /**
     * Represents the query parameters for finding reports.
     *
     * This property constructs a [StringValues] instance containing the query parameters
     * derived from the properties of the [FindReportsQuery] object.
     *
     * The parameters are encoded as key-value pairs where:
     * - `startRowIndex`:  The starting row index for the results (e.g., 0 for the first row).
     * - `maxRows`: The maximum number of rows to return.
     * - `accession` (optional): The accession number to filter by.
     * - `patientName` (optional): The patient name to filter by.
     * - `gender` (optional): The patient's gender to filter by (using the name of the [Gender] enum).
     * - `dateFrom` (optional): The start date of the date range to filter by (formatted by [LocalDatePattern.Soap.formatter]).
     * - `dateTo` (optional): The end date of the date range to filter by (formatted by [LocalDatePattern.Soap.formatter]).
     * - `sortColumn` (optional): Column used for sorting the result.
     **/
    val FindReportsQuery.params: StringValues
        get() = StringValues.build {
            append(PARAM_START_ROW_INDEX, startRowIndex.toString())
            append(PARAM_MAX_ROWS, maxRows.toString())
            accession?.let {
                append(PARAM_ACCESSION, it)
            }
            patientName?.let {
                append(PARAM_PATIENT_NAME, it)
            }
            gender?.let {
                append(PARAM_GENDER, it.name)
            }
            dateFrom?.let {
                append(PARAM_DATE_FROM, LocalDatePattern.Soap.formatter.format(it))
            }
            dateTo?.let {
                append(PARAM_DATE_TO, LocalDatePattern.Soap.formatter.format(it))
            }
            sortColumn?.let {
                append(PARAM_SORT_COLUMN, it.name)
            }
        }
}