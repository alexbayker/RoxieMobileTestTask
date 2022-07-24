package com.example.roxiemobiletesttask.models

import androidx.databinding.BaseObservable
import androidx.databinding.Bindable
import com.google.gson.annotations.SerializedName

class Price (
    @SerializedName("amount")
    val amount: Int,

    @SerializedName("currency")
    val currency: String
): BaseObservable() {
    @Bindable
    fun getCostString(): String
    {
        return "${amount / 100}.${String.format("%02d", amount % 100)} $currency"
    }
}