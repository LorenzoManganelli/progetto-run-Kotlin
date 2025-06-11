package com.example.runplusplus.database

import androidx.room.TypeConverter
import java.time.LocalDate
import java.time.LocalTime
import java.util.Date
//tutti i converter in un singolo file per comodità FORSE DA CAMBIARE
class Converters {

    @TypeConverter
    fun fromTimestamp(value: Long?): Date? {
        return value?.let { Date(it) }
    }

    @TypeConverter
    fun dateToTimestamp(date: Date?): Long? {
        return date?.time
    }

    @TypeConverter
    fun fromString(value: String?): LocalDate? {
        return value?.let { LocalDate.parse(it) }
    }

    @TypeConverter
    fun localDateToString(date: LocalDate?): String? {
        return date?.toString()
    }

    @TypeConverter
    fun fromLocalDate(value: LocalDate): String = value.toString()

    @TypeConverter
    fun toLocalDate(value: String): LocalDate = LocalDate.parse(value)

    @TypeConverter
    fun fromLocalTime(value: LocalTime): String = value.toString()

    @TypeConverter
    fun toLocalTime(value: String): LocalTime = LocalTime.parse(value)

    @TypeConverter
    fun fromListIntToString(lista: List<Int>?): String {
        return lista?.joinToString(",") ?: ""
    }

    @TypeConverter
    fun fromStringToListInt(data: String?): List<Int> {
        return data?.split(",")?.filter { it.isNotBlank() }?.map { it.toInt() } ?: emptyList()
    }
}
