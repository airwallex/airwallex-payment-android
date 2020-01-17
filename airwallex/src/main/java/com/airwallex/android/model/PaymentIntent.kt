package com.airwallex.android.model

import android.os.Parcelable
import androidx.annotation.IntRange
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentIntent internal constructor(

    @SerializedName("id")
    val id: String,

    @SerializedName("request_id")
    val requestId: String,

    @SerializedName("amount")
    val amount: Float,

    @SerializedName("currency")
    val currency: String,

    @SerializedName("merchant_order_id")
    val merchantOrderId: String,

    @SerializedName("customer_id")
    val customerId: String,

    @SerializedName("descriptor")
    val descriptor: String,

    @SerializedName("status")
    val status: String


    ) : AirwallexModel, Parcelable