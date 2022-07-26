package com.example.roxiemobiletesttask.converters

import androidx.room.TypeConverter
import com.example.roxiemobiletesttask.staticpackage.TextFunctions
import java.util.*

object DateRoomConverter
{
    @TypeConverter
    @JvmStatic
    fun fromDateToString(value: Date?): String
    {
        if (value == null)
            return "0"
        return "${value.time}"
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToDate(value: String): Date
    {
        if (TextFunctions.isLong(value))
            return Date(value.toLong())
        return Date(0)
    }
}