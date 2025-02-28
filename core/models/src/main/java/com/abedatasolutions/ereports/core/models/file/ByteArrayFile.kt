package com.abedatasolutions.ereports.core.models.file

data class ByteArrayFile(
    val byteArray: ByteArray,
    val type: FileType
) {
    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as ByteArrayFile

        if (!byteArray.contentEquals(other.byteArray)) return false
        if (type != other.type) return false

        return true
    }

    override fun hashCode(): Int {
        var result = byteArray.contentHashCode()
        result = 31 * result + type.hashCode()
        return result
    }
}
