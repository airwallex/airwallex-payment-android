package com.airwallex.paymentacceptance.model

import com.google.gson.annotations.SerializedName

data class Order(
    @SerializedName("products")
    val products: List<Product>,

    @SerializedName("shipping")
    val shipping: Shipping?,

    @SerializedName("type")
    val type: String
)