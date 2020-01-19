package com.airwallex.paymentacceptance.model

import android.os.Parcelable
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

@Parcelize
data class Address(

    @SerializedName("country_code")
    var countryCode: String,

    @SerializedName("state")
    var state: String,

    @SerializedName("city")
    var city: String,

    @SerializedName("street")
    var street: String,

    @SerializedName("postcode")
    var postcode: String
) : Parcelable