package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize
import java.util.*

@Parcelize
data class ClientSecret internal constructor(

    @SerializedName("client_secret")
    val value: String,

    @SerializedName("expired_time")
    val expiredTime: Date
) : AirwallexModel, Parcelable
