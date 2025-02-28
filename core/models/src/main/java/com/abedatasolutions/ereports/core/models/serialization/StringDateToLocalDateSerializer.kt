package com.abedatasolutions.ereports.core.models.serialization

import com.abedatasolutions.ereports.core.common.datetime.LocalDatePattern
import com.abedatasolutions.ereports.core.common.datetime.LocalDatePattern.Companion.distantDate
import com.abedatasolutions.ereports.core.common.logging.Logger
import kotlinx.datetime.Instant
import kotlinx.datetime.LocalDate
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object StringDateToLocalDateSerializer: KSerializer<LocalDate?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StringDate", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): LocalDate? {
        val dateString = decoder.runCatching {
            decodeString()
        }.getOrNull()
        if (dateString.isNullOrEmpty()) return null
        return try {
            LocalDatePattern.parse(dateString)
        }catch (e: Exception){
            Logger.log("Cannot Deserialize: $dateString")
            Logger.recordException(e)
            LocalDate.distantDate
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: LocalDate?) {
        value?.let {
            encoder.encodeString(it.toString())
        }?: encoder.encodeNull()
    }
}