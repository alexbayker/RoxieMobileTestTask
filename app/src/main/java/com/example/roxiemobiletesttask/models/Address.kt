package com.example.roxiemobiletesttask.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName

class Address (
    @Bindable
    @SerializedName("city")
    val city: String,

    @Bindable
    @SerializedName("address")
    val address: String
): BaseObservable()