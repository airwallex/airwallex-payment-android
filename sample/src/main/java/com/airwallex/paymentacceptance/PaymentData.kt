package com.airwallex.paymentacceptance

import com.airwallex.android.model.*

object PaymentData {

    var shipping: Shipping? = Shipping.Builder()
        .setFirstName("John")
        .setLastName("Doe")
        .setPhone("13800000000")
        .setEmail("jim631@sina.com")
        .setAddress(
            Address.Builder()
                .setCountryCode("CN")
                .setState("Shanghai")
                .setCity("Shanghai")
                .setStreet("Pudong District")
                .setPostcode("100000")
                .build()
        )
        .build()

    var billing: PaymentMethod.Billing? = PaymentMethod.Billing.Builder()
        .setFirstName("John")
        .setLastName("Doe")
        .setPhone("13800000000")
        .setEmail("jim631@sina.com")
        .setAddress(
            Address.Builder()
                .setCountryCode("CN")
                .setState("Shanghai")
                .setCity("Shanghai")
                .setStreet("Pudong District")
                .setPostcode("100000")
                .build()
        )
        .build()

    var products = mutableListOf(
        Product.Builder()
            .setCode("123")
            .setName("AirPods Pro")
            .setDesc("Buy AirPods Pro, per month with trade-in")
            .setSku("piece")
            .setType("Free engraving")
            .setUnitPrice(399.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build(),
        Product.Builder()
            .setCode("123")
            .setName("HomePod")
            .setDesc("Buy HomePod, per month with trade-in")
            .setSku("piece")
            .setType("White")
            .setUnitPrice(469.00)
            .setUrl("www.aircross.com")
            .setQuantity(1)
            .build()
    )
}