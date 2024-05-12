package com.example.geomaps.util

import androidx.room.TypeConverter
import java.time.LocalDateTime
import java.time.ZoneOffset

object LocalDateTimeConverter {

    @TypeConverter
    fun toDate(dateEpochSecs: Long?): LocalDateTime? {
        return if (dateEpochSecs == null) {
            null
        } else {
            LocalDateTime.ofEpochSecond(dateEpochSecs, 0, ZoneOffset.ofHours(3))
        }
    }

    @TypeConverter
    fun toDateString(date: LocalDateTime?): Long? {
        return date?.toEpochSecond(ZoneOffset.ofHours(3))
    }
}