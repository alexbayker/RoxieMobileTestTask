package com.example.roxiemobiletesttask.models

import android.content.Context
import android.content.Intent
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import android.view.View
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.roxiemobiletesttask.API
import com.example.roxiemobiletesttask.activities.OrderActivity
import com.example.roxiemobiletesttask.converters.DateGsonConverter
import com.example.roxiemobiletesttask.db.AppDatabase
import com.example.roxiemobiletesttask.staticpackage.Constants
import com.example.roxiemobiletesttask.staticpackage.TextFunctions
import com.google.gson.Gson
import com.google.gson.annotations.SerializedName
import com.google.gson.reflect.TypeToken
import okhttp3.ResponseBody
import org.json.JSONArray
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.BufferedReader
import java.io.InputStreamReader
import java.net.ConnectException
import java.net.SocketTimeoutException
import java.net.UnknownHostException
import java.util.*
import java.util.concurrent.CopyOnWriteArrayList

@Entity(tableName = Constants.ORDERSBASENAME)
class Order (
    @PrimaryKey
    @Bindable
    @SerializedName("id")
    val id: Int,

    @Bindable
    @SerializedName("startAddress")
    val startAddress: Address,

    @Bindable
    @SerializedName("endAddress")
    val endAddress: Address,

    @Bindable
    @SerializedName("orderTime")
    val orderTime: Date,

    @Bindable
    @SerializedName("vehicle")
    val vehicle: Vehicle,

    @Bindable
    @SerializedName("price")
    val price: Price
): Comparable<Order>, Parcelable, BaseObservable()
{
    constructor(parcel: Parcel) : this(
        parcel.readString()!!.replace("[^0-9]+".toRegex(), "").toInt(),
        Gson().fromJson(parcel.readString(), Address::class.java),
        Gson().fromJson(parcel.readString(), Address::class.java),
        Date(parcel.readString()!!.toLong()),
        Gson().fromJson(parcel.readString(), Vehicle::class.java),
        Gson().fromJson(parcel.readString(), Price::class.java),
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeStringArray(
            arrayOf<String>(
                this.id.toString(),
                Gson().toJson(this.startAddress),
                Gson().toJson(this.endAddress),
                "${this.orderTime.time}",
                Gson().toJson(this.vehicle),
                Gson().toJson(this.price)
            )
        )
    }

    override fun describeContents(): Int {
        return 0
    }

    @Bindable
    fun getDateString(): String
    {
        return if (this.orderTime.time > 0)
            Constants.LASTUPDATEFORMAT.format(this.orderTime)
        else
            ""
    }

    fun openOrderActivity(view: View, order: Order) {
        val intent = Intent(view.context, OrderActivity::class.java)
        intent.putExtra("order", order)
        view.context.startActivity(intent)
    }

    override fun compareTo(other: Order): Int {
        return other.orderTime.time.compareTo(this.orderTime.time)
    }

    companion object {
        @JvmField
        val CREATOR: Parcelable.Creator<Order> =
            object : Parcelable.Creator<Order> {
                override fun createFromParcel(source: Parcel): Order {
                    return Order(source)
                }

                override fun newArray(size: Int): Array<Order?> {
                    return arrayOfNulls(size)
                }
            }

        fun defaultOrders(context: Context): List<Order> {
            val list = CopyOnWriteArrayList<Order>()
            try {
                val reader = BufferedReader(InputStreamReader(context.assets.open("orders.json")))
                list.addAll(
                    DateGsonConverter.builder.create().fromJson<CopyOnWriteArrayList<Order>>(reader,
                        object : TypeToken<CopyOnWriteArrayList<Order>>() {}.type
                    )
                )
            } catch (e: Exception) {
                Log.e("ParseError", "Hardcode orders parse error: ", e)
            }
            return list
        }

        fun getOrdersFromServer(context: Context, needtoast: Boolean = false, donecallback: (List<Order>) -> Unit = {}, errorcallback: () -> Unit = {})
        {
            Retrofit.Builder().baseUrl(Constants.getBaseUrl(Constants.ORDERS_URL)).build().create(API::class.java)
                .apiRequest(Constants.ORDERS_URL).enqueue(object :
                    Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>)
                    {
                        val answer = response.body()!!.string()

                        try {
                            val list = DateGsonConverter.builder.create().fromJson<CopyOnWriteArrayList<Order>>(JSONArray(answer).toString(),
                                object : TypeToken<CopyOnWriteArrayList<Order>>() {}.type)

                            if (list.isNotEmpty())
                            {
                                val db = AppDatabase.invoke(context).ordersDB()
                                db.clear()
                                db.insertAll(list)
                                TextFunctions.saveText(Constants.LASTUPDATESHAREDPREFS, "${Date().time}", context)
                                if (needtoast)
                                    TextFunctions.showText(context, "Orders from server updated!")

                                donecallback(list)
                            }
                            else
                                errorcallback()
                        } catch (e: Exception) {
                            val frombase = AppDatabase.invoke(context).ordersDB().getAll()
                            if (frombase.isNotEmpty())
                                donecallback(frombase)
                            else
                            {
                                errorcallback()
                                if (needtoast)
                                    TextFunctions.showText(context, "Orders from server parsing error!")
                            }
                            Log.e("ParseError", "Orders from server parsing error: ", e)
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        val frombase = AppDatabase.invoke(context).ordersDB().getAll()
                        if (frombase.isNotEmpty())
                            donecallback(frombase)
                        else
                            errorcallback()
                        if ((t is SocketTimeoutException || t is UnknownHostException || t is ConnectException)
                            && needtoast)
                        {
                            TextFunctions.showText(context, "Server not found!")
                        }
                        else {
                            if (needtoast)
                                TextFunctions.showText(context, "Loading error!")
                            Log.e("LoadingError", "Loading weather error: ", t)
                        }
                    }
                })
        }
    }
}