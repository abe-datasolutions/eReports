package com.abedatasolutions.ereports.core.models.serialization

import assertk.assertThat
import assertk.assertions.isEqualTo
import com.abedatasolutions.ereports.core.common.datetime.LocalDatePattern
import kotlinx.datetime.LocalDate
import kotlinx.serialization.json.Json
import org.junit.Test

class StringDateToLocalDateSerializerTest{
    @Test
    fun testSerializeAndDeserializeOnSoapDate(){
        val dateString = "\"09/08/1962\""

        println("Date String: $dateString")
        println("Matching Pattern: ${LocalDatePattern.find(dateString)}")

        val date = Json.decodeFromString(StringDateToLocalDateSerializer, dateString)
        val expectedDate = LocalDate(1962, 9, 8)

        assertThat(date).isEqualTo(expectedDate)

        val serializedDate = Json.encodeToString(StringDateToLocalDateSerializer, date)

        println("Serialized Date: $serializedDate")
        println("Matching Pattern: ${LocalDatePattern.find(serializedDate)}")

        val deserializedDate = Json.decodeFromString(StringDateToLocalDateSerializer, serializedDate)

        assertThat(deserializedDate).isEqualTo(date)
    }
}