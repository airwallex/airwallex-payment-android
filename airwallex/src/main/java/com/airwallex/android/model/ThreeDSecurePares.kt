package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class ThreeDSecurePares internal constructor(

    @SerializedName("paresId")
    val paresId: String,

    @SerializedName("pares")
    val pares: String
) : AirwallexModel, Parcelable
