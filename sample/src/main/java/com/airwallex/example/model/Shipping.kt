package com.airwallex.example.model

import com.google.gson.annotations.SerializedName

data class Shipping(

    @SerializedName("shipping_method")
    val shippingMethod: String,

    @SerializedName("first_name")
    val firstName: String,

    @SerializedName("last_name")
    val lastName: String,

    @SerializedName("phone_number")
    val phone: String,

    @SerializedName("address")
    val shippingAddress: ShippingAddress
)
