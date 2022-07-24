package com.example.roxiemobiletesttask.staticpackage

import android.content.Context
import android.os.Environment
import com.example.roxiemobiletesttask.App
import java.text.SimpleDateFormat
import java.util.*

class Constants
{
    companion object
    {
        const val ORDERSBASENAME = "orders"

        const val LASTUPDATESHAREDPREFS = "lastcitybaseupdate"

        const val MEMORY_PERMISSION             = 11
        const val REMOVE_FILES_SERVICES_NOTE_ID = 17

        const val IMAGES_LIFETIME:  Long = 600 * 1000
        const val CONTENT_LIFETIME: Long = 60 * 60 * 1000

        const val IMAGE_URL = "https://www.roxiemobile.ru/careers/test/images/"
        const val ORDERS_URL = "https://www.roxiemobile.ru/careers/test/orders.json"
        private var IMAGE_DIRECTORY = ""
        private val DOWNLOADS_DIRECTORY = "${Environment.getExternalStorageDirectory().path}/Download/"

        fun getImageDirectory(context: Context? = App.instance?.baseContext): String
        {
            if (context != null)
            {
                if (IMAGE_DIRECTORY.isEmpty())
                    IMAGE_DIRECTORY = "${context.filesDir?.path}/images/"
            }
            return IMAGE_DIRECTORY
        }

        fun getBaseUrl(url: String): String
        {
            return url.replace("[^\\\\/]+$".toRegex(), "")
        }

        val LASTUPDATEFORMAT  = SimpleDateFormat("HH:mm:ss dd-MM-yyyy", Locale("en", "EN"))
    }
}