package com.abedatasolutions.ereports.core.models.serialization

import com.abedatasolutions.ereports.core.common.datetime.DateTimePattern
import com.abedatasolutions.ereports.core.common.datetime.LocalDateTimePattern
import com.abedatasolutions.ereports.core.common.datetime.InstantPattern
import com.abedatasolutions.ereports.core.common.logging.Logger
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toInstant
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object StringDateTimeToInstantSerializer: KSerializer<Instant?> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("StringDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant? {
        val dateString = decoder.runCatching {
            decodeString()
        }.getOrNull()
        if (dateString.isNullOrEmpty()) return null
        return try {
            val pattern = DateTimePattern.find(dateString)
                ?: throw IllegalArgumentException("Cannot find pattern for: $dateString")

            when(pattern){
                DateTimePattern.Instant -> InstantPattern.parse(dateString)
                DateTimePattern.Local -> LocalDateTimePattern.parse(dateString).toInstant(
                    TimeZone.currentSystemDefault()
                )
            }
        }catch (e: Exception){
            Logger.log("Cannot Deserialize: $dateString")
            Logger.recordException(e)
            null
        }
    }

    @OptIn(ExperimentalSerializationApi::class)
    override fun serialize(encoder: Encoder, value: Instant?) {
        value?.let {
            encoder.encodeString(it.toString())
        }?: encoder.encodeNull()
    }
}