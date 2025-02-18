package com.abedatasolutions.ereports.build.logic.convention

data class BuildConfigField(
    val type: BuildConfigType,
    val name: String,
    val value: String
){
    val wrappedValue: String = when(type){
        BuildConfigType.String -> "\"$value\""
        BuildConfigType.Boolean,
        BuildConfigType.Double,
        BuildConfigType.Int -> value
    }
}