package com.airwallex.android.model

import android.os.Parcelable
import androidx.annotation.IntRange
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Card internal constructor(

    @SerializedName("cvc")
    val cvc: String,

    @SerializedName("exp_month")
    @get:IntRange(from = 1, to = 12)
    val expMonth: Int,

    @SerializedName("exp_year")
    val expYear: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("number")
    val number: String
) : AirwallexModel, Parcelable