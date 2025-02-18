package com.abedatasolutions.ereports.build.logic.convention

enum class BuildConfigType {
    String,
    Boolean,
    Double,
    Int{
        override val generic: kotlin.String = "int"
    };

    open val generic: kotlin.String = name
}