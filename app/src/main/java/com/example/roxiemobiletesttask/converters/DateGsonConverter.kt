package com.example.roxiemobiletesttask.converters

import android.util.Log
import com.google.gson.*
import java.lang.reflect.Type
import java.text.SimpleDateFormat
import java.util.*

object DateGsonConverter
{
    var dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale("ru", "RU"))

    var dateSerializer =
        JsonSerializer { src: Date, typeOfSrc: Type?, context: JsonSerializationContext? ->
            JsonPrimitive(dateFormat.format(src))
        }

    var dateDeserializer =
        JsonDeserializer { json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext? ->
            try {
                if (json != null) {
                    val date = json.asString
                    if (date.length >= 25)
                        return@JsonDeserializer dateFormat.parse(date.substring(0, 25))
                }
            } catch (e: Exception) {
                Log.e("ParseException", "Date deserialize from json error: $json ", e)
            }
            return@JsonDeserializer Date(0)
        }

    val builder: GsonBuilder = GsonBuilder()
        .registerTypeAdapter(Date::class.java, dateSerializer)
        .registerTypeAdapter(Date::class.java, dateDeserializer)
}