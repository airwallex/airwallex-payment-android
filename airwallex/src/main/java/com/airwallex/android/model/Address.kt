package com.airwallex.android.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address internal constructor(

    @SerializedName("city")
    val city: String,

    @SerializedName("country_code")
    val countryCode: String,

    @SerializedName("postcode")
    val postcode: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("street")
    val street: String
) : AirwallexModel, Parcelable