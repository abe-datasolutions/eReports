package com.abedatasolutions.ereports.core.models.file

enum class FileType(
    val mimeType: String,
    val extension: String
) {
    Jpeg(
        mimeType = "image/jpeg",
        extension = "jpg"
    ),
    Json(
        mimeType = "application/json",
        extension = "json"
    ),
    Csv(
        mimeType = "text/csv",
        extension = "csv"
    ),
}