package com.airwallex.example.model

data class ShippingAddress(
    val countryCode: String,
    val state: String,
    val city: String,
    val street: String,
    val postcode: String
)