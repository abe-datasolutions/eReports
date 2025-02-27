package com.abedatasolutions.ereports.core.common.datetime

import kotlinx.datetime.Instant

/**
 * Enum class representing different patterns for parsing date strings into `Instant` objects.
 *
 * This enum provides a way to define and use various date string formats and their corresponding
 * parsing logic. Each enum entry represents a specific date/time pattern and has a method to parse
 * a string matching that pattern into an `Instant` object.
 *
 * @property pattern The regular expression pattern used to identify if a date string matches the format.
 */
enum class InstantPattern(val pattern: String) {
    ISO("""^\d{4}-\d{2}-\d{2}T\d{2}:\d{2}:\d{2}(?:\.\d+)?Z$""") {
        override fun parseInstant(dateString: String): Instant = Instant.parse(dateString)
    },
    Soap("""^/Date\((\d+)\)/$""") {
        override fun parseInstant(dateString: String): Instant {
            val timestamp = dateString
                .removePrefix(SOAP_PREFIX)
                .removeSuffix(SOAP_SUFFIX)
                .toLong()
            return Instant.fromEpochMilliseconds(timestamp)
        }
    };

    protected abstract fun parseInstant(dateString: String): Instant

    companion object {
        private const val SOAP_PREFIX = "/Date("
        private const val SOAP_SUFFIX = ")/"

        fun parse(dateString: String): Instant = entries.find {
            it.pattern.toRegex().matches(dateString)
        }?.parseInstant(dateString) ?: throw IllegalArgumentException("Cannot find pattern for: $dateString")
    }
}