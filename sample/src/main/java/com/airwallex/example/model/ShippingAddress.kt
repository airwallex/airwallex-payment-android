package com.airwallex.example.model

import com.google.gson.annotations.SerializedName

data class ShippingAddress(

    @SerializedName("country_code")
    val countryCode: String,

    @SerializedName("state")
    val state: String,

    @SerializedName("city")
    val city: String,

    @SerializedName("street")
    val street: String,

    @SerializedName("postcode")
    val postcode: String
)