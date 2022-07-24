package com.example.roxiemobiletesttask.converters

import androidx.room.TypeConverter
import com.example.roxiemobiletesttask.models.Vehicle
import com.google.gson.Gson

object VehicleRoomConverter {
    @TypeConverter
    @JvmStatic
    fun fromVehicleToString(value: Vehicle): String
    {
        return Gson().toJson(value)
    }

    @TypeConverter
    @JvmStatic
    fun fromStringToVehicle(value: String): Vehicle
    {
        return Gson().fromJson(value, Vehicle::class.java)
    }
}