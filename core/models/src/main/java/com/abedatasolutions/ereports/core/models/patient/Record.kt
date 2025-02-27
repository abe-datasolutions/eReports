package com.abedatasolutions.ereports.core.models.patient


import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a single record of a medical test result.
 * This data class is designed to store information about a specific procedure and its corresponding results,
 * including reference ranges, units, and additional details.
 *
 * @property procedure The name or description of the medical procedure performed.
 * @property inRangeResult The result of the procedure, indicating whether it was within the reference range.
 * @property outsideReferenceRange  Indicates whether the result is outside the normal reference range (e.g., "High", "Low", or "").
 * @property referenceRange The normal reference range for this procedure, usually in the form of "X-Y".
 * @property referenceDetails Optional list of strings providing more detailed information about the reference range.
 * @property unit The unit of measurement for the result (e.g., "mg/dL", "mmol/L").
 * @property resultDetails Optional list of strings providing more detailed information about the result.
 * @property isSubRecord Indicates if this record is a sub-record within a larger record structure.
 */
@Serializable
data class Record(
    @SerialName("Procedure")
    val procedure: String = "",
    @SerialName("InRangeResult")
    val inRangeResult: String = "",
    @SerialName("OutsideReferenceRange")
    val outsideReferenceRange: String = "",
    @SerialName("ReferenceRange")
    val referenceRange: String = "",
    @SerialName("ReferenceDetails")
    val referenceDetails: List<String>? = null,
    @SerialName("Unit")
    val unit: String = "",
    @SerialName("ResultDetails")
    val resultDetails: List<String>? = null,
    @SerialName("IsSubRecord")
    val isSubRecord: Boolean = false
)