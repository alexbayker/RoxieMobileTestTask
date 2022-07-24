package com.example.roxiemobiletesttask.db

import androidx.room.*
import com.example.roxiemobiletesttask.models.Order
import com.example.roxiemobiletesttask.staticpackage.Constants

@Dao
interface OrdersDB
{
    @Query("SELECT * FROM ${Constants.ORDERSBASENAME}")
    fun getAll(): List<Order>

    @Query("SELECT * FROM ${Constants.ORDERSBASENAME} WHERE id = :id")
    fun getById(id: Long): List<Order>

    @Query("SELECT COUNT(id) FROM ${Constants.ORDERSBASENAME}")
    fun getCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertAll(orders: List<Order>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(order: Order)

    @Update
    fun update(order: Order)

    @Delete
    fun delete(order: Order)

    @Query("DELETE FROM ${Constants.ORDERSBASENAME}")
    fun clear()
}