package com.airwallex.android.model

import android.os.Parcelable
import androidx.annotation.IntRange
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class PaymentMethod internal constructor(

    @SerializedName("billing")
    val billing: Billing,

    @SerializedName("card")
    val card: Card,

    @SerializedName("type")
    val type: String

) : AirwallexModel, Parcelable