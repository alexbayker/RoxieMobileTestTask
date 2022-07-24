package com.example.roxiemobiletesttask.models

import android.app.Activity
import android.graphics.BitmapFactory
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import androidx.databinding.BindingAdapter
import com.example.roxiemobiletesttask.API
import com.example.roxiemobiletesttask.staticpackage.Constants
import com.google.gson.annotations.SerializedName
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.io.*
import java.util.*

class Vehicle (
    @Bindable
    @SerializedName("regNumber")
    val regNumber: String,

    @Bindable
    @SerializedName("modelName")
    val modelName: String,

    @Bindable
    @SerializedName("photo")
    private val photo: String,

    @Bindable
    @SerializedName("driverName")
    val driverName: String
): BaseObservable() {

    private fun getPhotoUrl(): String
    {
        return "${Constants.IMAGE_URL}${this.photo}"
    }

    private fun getSavedPhotoUrl(): String
    {
        return "${Constants.getImageDirectory()}${this.photo}"
    }

    @Bindable
    fun getVisibleRegNumber(): String
    {
        return this.regNumber.uppercase()
    }


    fun loadImage(donecallback: (String) -> Unit = {}, errorcallback: () -> Unit = {})
    {
        try {
            if (File(this.getSavedPhotoUrl()).exists())
                donecallback(this.getSavedPhotoUrl())
            else
            {
                Retrofit.Builder().baseUrl(Constants.getBaseUrl(this.getPhotoUrl())).build().create(
                    API::class.java).downloadFile(this.getPhotoUrl()).enqueue(object : Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>)
                    {
                        try {
                            val body = response.body()
                            if (body != null && response.isSuccessful) {
                                Thread {
                                    var inputStream: InputStream? = null
                                    var outputStream: OutputStream? = null
                                    try {
                                        val imageDirectory = File(Constants.getImageDirectory())
                                        if (!imageDirectory.exists())
                                            imageDirectory.mkdirs()
                                        val savedImage = File(getSavedPhotoUrl())
                                        if (!savedImage.exists())
                                            savedImage.createNewFile()

                                        inputStream = body.byteStream()
                                        outputStream = FileOutputStream(savedImage)
                                        val buffer = ByteArray(4096)
                                        var read: Int
                                        while (inputStream.read(buffer).also { read = it } != -1)
                                            outputStream.write(buffer, 0, read)

                                        if (savedImage.exists())
                                            donecallback(savedImage.absolutePath)
                                        else
                                            errorcallback()

                                    } catch (e: Exception) {
                                        Log.e("HandlingVehicleImageErr", "Exception in saving vehicle preview: ", e)
                                        errorcallback()
                                    }
                                    finally {
                                        try {
                                            inputStream?.close()
                                            outputStream?.close()
                                        } catch (e: IOException) {
                                            Log.e("HandlingVehicleImageErr", "Close streams error in loading vehicle preview: ", e)
                                        }
                                    }
                                }.start()
                            }
                        } catch (e: Exception)
                        {
                            Log.e("HandlingVehicleImageErr", "Handling Vehicle Preview Image Error: ", e)
                            errorcallback()
                        }
                    }

                    override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                        //if (imageView != null && imageView.context != null)
                        //TextFunctions.showText(imageView.context, "Remove files error!")
                        Log.e("LoadVehiclePreviewError", "Server not found!", t)
                        errorcallback()
                    }
                })
            }
        }
        catch (e: Exception)
        {
            Log.e("DownloadImageError", "Download vehicle preview image error: ", e)
        }
    }

    companion object {
        @JvmStatic
        @BindingAdapter("android:src", "progressbar")
        fun loadImage(view: ImageView, vehicle: Vehicle, loadingbar: ProgressBar) {
            vehicle.loadImage ({ fileUrl ->
                if (fileUrl.isNotEmpty() && view.context is Activity)
                {
                    (view.context as Activity).runOnUiThread {
                        val myBitmap = BitmapFactory.decodeFile(fileUrl)
                        view.setImageBitmap(myBitmap)
                        loadingbar.visibility = View.GONE
                        view.visibility = View.VISIBLE
                    }
                }
            },
            {
                if (view.context is Activity)
                {
                    (view.context as Activity).runOnUiThread {
                        view.visibility = View.GONE
                        loadingbar.visibility = View.VISIBLE
                    }
                }
            })
        }
    }
}