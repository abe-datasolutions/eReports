package com.abedatasolutions.ereports.core.common.datetime

enum class DateTimePattern(vararg val patterns: String){
    Instant(
        *InstantPattern.entries.map { it.pattern }.toTypedArray()
    ),
    Local(
        *LocalDateTimePattern.entries.map { it.pattern }.toTypedArray()
    );

    companion object{
        fun find(dateTimeString: String): DateTimePattern? = entries.find { pattern ->
            pattern.patterns.any {
                it.toRegex().matches(dateTimeString)
            }
        }
    }
}