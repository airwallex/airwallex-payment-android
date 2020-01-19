package com.airwallex.paymentacceptance.model

import com.google.gson.annotations.SerializedName

data class Product(
    @SerializedName("code")
    val code: Int,

    @SerializedName("name")
    val name: String,

    @SerializedName("desc")
    val desc: String,

    @SerializedName("sku")
    val sku: String,

    @SerializedName("type")
    val type: String,

    @SerializedName("unit_price")
    val unitPrice: Double,

    @SerializedName("url")
    val url: String,

    @SerializedName("quantity")
    val quantity: Int
)