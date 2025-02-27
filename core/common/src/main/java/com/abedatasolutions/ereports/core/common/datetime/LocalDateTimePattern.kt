package com.abedatasolutions.ereports.core.common.datetime

import kotlinx.datetime.LocalDateTime
import kotlinx.datetime.LocalTime
import kotlinx.datetime.format.DateTimeFormat
import kotlinx.datetime.format.char

/**
 * Enum class representing different patterns for date and time strings.
 *
 * This enum provides predefined patterns for common date and time string formats, along with
 * their corresponding `DateTimeFormat` for parsing.
 *
 * @property pattern The regular expression pattern that defines the format of the date/time string.
 */
enum class LocalDateTimePattern(val pattern: String) {
    /**
     * Represents the ISO 8601 format (e.g., `2023-10-27T10:30`, `2023-10-27T10:30:00`, `2023-10-27T10:30:00.123`).
     */
    ISO("""^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}(?::\d{2}(?:\.\d+)?)?$""") {
        override val formatter: DateTimeFormat<LocalDateTime>
            get() = LocalDateTime.Formats.ISO
    },

    /**
     * Represents a custom date/time format (e.g., `10/27/2023 10:30`, `10/27/2023 10:30:00`, `10/27/2023 10:30:00.123`).
     */
    Soap("""^\d{2}/\d{2}/\d{4} \d{2}:\d{2}(?::\d{2}(?:\.\d+)?)?$""") {
        override val formatter: DateTimeFormat<LocalDateTime> by lazy {
            LocalDateTime.Format {
                date(LocalDatePattern.Soap.formatter)
                char(' ')
                time(LocalTime.Formats.ISO)
            }
        }
    };

    abstract val formatter: DateTimeFormat<LocalDateTime>

    companion object {
        fun find(dateTimeString: String): LocalDateTimePattern? = entries.find {
            it.pattern.toRegex().matches(dateTimeString)
        }

        /**
         * Parses a date-time string into a LocalDateTime object.
         *
         * This function attempts to parse the provided [dateTimeString] using a set of predefined
         * date-time formatters. It searches for a matching formatter.
         * If a matching formatter is found, it is used to parse the string.
         *
         * @param dateTimeString The string to parse into a LocalDateTime object.
         * @return The parsed [LocalDateTime] object.
         * @throws IllegalArgumentException If no matching formatter is found for the given [dateTimeString].
         * @throws DateTimeParseException If a matching formatter is found but fails to parse the string according to its format.
         *
         * Example:
         * ```kotlin
         * // Assuming a formatter for "yyyy-MM-dd HH:mm:ss" is registered
         * val dateTime = parse("2023-10-27 10:30:00")
         * println(dateTime) // Output: 2023-10-27T10:30
         *
         * // Example with an invalid string
         * try {
         *     parse("invalid date")
         * } catch (e: IllegalArgumentException) {
         *     println(e.message) // Output: Cannot find pattern for: invalid date
         * }
         *
         * //Example with wrong format for existing pattern
         * try {
         *     parse("2023/10/27")
         * } catch (e: DateTimeParseException){
         *      println("Error parsing date format")
         * }
         * ```
         */
        fun parse(dateTimeString: String): LocalDateTime =
            find(dateTimeString)?.formatter?.parse(dateTimeString)
                ?: throw IllegalArgumentException("Cannot find pattern for: $dateTimeString")
    }
}