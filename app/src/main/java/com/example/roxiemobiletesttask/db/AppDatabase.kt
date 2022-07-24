package com.example.roxiemobiletesttask.db

import android.content.Context
import androidx.room.*
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.roxiemobiletesttask.App
import com.example.roxiemobiletesttask.converters.AddressRoomConverter
import com.example.roxiemobiletesttask.converters.DateRoomConverter
import com.example.roxiemobiletesttask.converters.PriceRoomConverter
import com.example.roxiemobiletesttask.converters.VehicleRoomConverter
import com.example.roxiemobiletesttask.models.Order
import com.example.roxiemobiletesttask.staticpackage.Constants
import com.example.roxiemobiletesttask.staticpackage.TextFunctions

@Database(entities = [Order::class], version = 1, exportSchema = false)
@TypeConverters(AddressRoomConverter::class, DateRoomConverter::class, PriceRoomConverter::class, VehicleRoomConverter::class)
abstract class AppDatabase: RoomDatabase()
{
    abstract fun ordersDB(): OrdersDB

    companion object {
        private val migration2_3 = object: Migration(2, 3) {
            override fun migrate(database: SupportSQLiteDatabase) {
                if (App.instance != null)
                    TextFunctions.saveText(Constants.LASTUPDATESHAREDPREFS, "0", App.instance!!)
            }
        }

        @Volatile
        private var instance: AppDatabase? = null
        private val LOCK = Any()

        operator fun invoke(context: Context) = instance ?: synchronized(LOCK) {
            instance ?: buildDatabase(context).also { instance = it}
        }

        private fun buildDatabase(context: Context) = Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "database.db"
        )
            .allowMainThreadQueries()
            .addMigrations(migration2_3)
            .build()
    }
}

