package com.example.roxiemobiletesttask.converters

import androidx.room.TypeConverter
import com.example.roxiemobiletesttask.models.Price
import com.google.gson.Gson

object PriceRoomConverter {
    @TypeConverter
    @JvmStatic
    fun fromPriceToString(value: Price): String
    {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToPrice(value: String): Price
    {
        return Gson().fromJson(value, Price::class.java)
    }
}