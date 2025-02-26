package com.abedatasolutions.ereports.core.serialization

import com.abedatasolutions.ereports.core.common.logging.Logger
import kotlinx.datetime.Instant
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder

object SoapDateTimeToInstantSerializer: KSerializer<Instant> {
    private const val PREFIX = "/Date("
    private const val SUFFIX = ")/"

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("SoapDateTime", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): Instant {
        val dateString = decoder.decodeString()
        return try {
            val timestamp = dateString
                .removePrefix(PREFIX)
                .removeSuffix(SUFFIX)
                .toLong()
            Instant.fromEpochMilliseconds(timestamp)
        }catch (e: Exception){
            Logger.log("Cannot Deserialize: $dateString")
            Logger.recordException(e)
            Instant.DISTANT_PAST
        }
    }

    override fun serialize(encoder: Encoder, value: Instant) {
        encoder.encodeString(
            buildString {
                append(PREFIX)
                append(value.toEpochMilliseconds())
                append(SUFFIX)
            }
        )
    }
}