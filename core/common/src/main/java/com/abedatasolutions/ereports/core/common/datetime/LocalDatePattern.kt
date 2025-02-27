package com.abedatasolutions.ereports.core.common.datetime

import kotlinx.datetime.LocalDate
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char

/**
 * Enum class representing different date patterns and their corresponding formatters.
 *
 * This enum defines a set of known date patterns, along with regular expressions to identify them
 * and `DateTimeFormat` objects to parse dates in those formats.
 *
 * @property pattern The regular expression pattern used to identify dates in this format.
 */
enum class LocalDatePattern(val pattern: String) {
    /**
     * Represents the ISO 8601 format `2025-01-31`
     */
    ISO("""^\d{4}-\d{2}-\d{2}$""") {
        override val formatter: DateTimeFormat<LocalDate>
            get() = LocalDate.Formats.ISO
    },

    /**
     * Represents a custom date format `01/31/2025`
     */
    Soap("""^\d{2}/\d{2}/\d{4}$""") {
        override val formatter: DateTimeFormat<LocalDate> by lazy {
            LocalDate.Format {
                monthNumber()
                char('/')
                dayOfMonth()
                char('/')
                year()
            }
        }
    };

    abstract val formatter: DateTimeFormat<LocalDate>

    companion object {
        fun find(dateString: String): LocalDatePattern? = entries.find {
            it.pattern.toRegex().matches(dateString)
        }

        /**
         * Parses a date string into a [LocalDate] object based on a predefined set of date patterns.
         *
         * This function attempts to match the input `dateString` against a collection of known date patterns.
         * If a match is found, it uses the corresponding [DateTimeFormatter] to parse the string and return a [LocalDate].
         *
         * @param dateString The date string to parse.
         * @return A [LocalDate] object representing the parsed date.
         * @throws IllegalArgumentException If no matching date pattern is found for the input `dateString`.
         *
         * Example Usage:
         * ```
         * val date1 = parse("2023-10-27") // Returns LocalDate.of(2023, 10, 27)
         * val date2 = parse("10/27/2023") // Returns LocalDate.of(2023, 10, 27)
         * try{
         *   val invalidDate = parse("invalid-date") // Throws IllegalArgumentException
         * } catch(e: IllegalArgumentException){
         *   println(e.message)
         * }
         * ```
         **/
        fun parse(dateString: String): LocalDate = find(dateString)?.formatter?.parse(dateString)
            ?: throw IllegalArgumentException("Cannot find pattern for: $dateString")
    }
}