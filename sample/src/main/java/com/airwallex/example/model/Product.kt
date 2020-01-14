package com.airwallex.example.model

data class Product(
    val code: Int,
    val name: String,
    val desc: String,
    val sku: String,
    val type: String,
    val unitPrice: Int,
    val url: String,
    val quantity: Int
)