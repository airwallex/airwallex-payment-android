package com.airwallex.example.model

data class Shipping(
    val shippingMethod: String,
    val firstName: String,
    val lastName: String,
    val phone: String,
    val shippingAddress: ShippingAddress
)
