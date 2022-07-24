package com.example.roxiemobiletesttask.converters

import androidx.room.TypeConverter
import com.example.roxiemobiletesttask.models.Address
import com.google.gson.Gson

object AddressRoomConverter {
    @TypeConverter
    @JvmStatic
    fun fromAddressToString(value: Address): String
    {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToAddress(value: String): Address
    {
        return Gson().fromJson(value, Address::class.java)
    }
}