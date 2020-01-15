package com.airwallex.example

import com.airwallex.example.model.Product
import com.airwallex.example.model.Shipping
import com.airwallex.example.model.ShippingAddress

object TestData {

    val shipping = Shipping(
        shippingMethod = "Space-X Rockets",
        firstName = "Yima",
        lastName = "Dangxian",
        phone = "8617601215499",
        shippingAddress = ShippingAddress(
            countryCode = "CN",
            state = "Shanghai",
            city = "Shanghai",
            street = "Shanghai, Shanghai, China",
            postcode = "20000"
        )
    )

    val products = mutableListOf(
        Product(
            code = 123,
            name = "IPhone XR",
            desc = "Buy iPhone XR, per month with trade-in",
            sku = "piece",
            type = "physical",
            unitPrice = 100,
            url = "www.aircross.com",
            quantity = 1
        ),
        Product(
            code = 123,
            name = "IPad Air 5",
            desc = "Buy iPad, Get free two-business-day delivery on any in‑stock iPad ordered by 5:00 p.m.",
            sku = "piece",
            type = "physical",
            unitPrice = 200,
            url = "www.aircross.com",
            quantity = 2
        ),
        Product(
            code = 123,
            name = "MacBook Pro",
            desc = "Buy iMac Pro. 3.2GHz 8-core Intel Xenon W processor",
            sku = "piece",
            type = "physical",
            unitPrice = 1000,
            url = "www.aircross.com",
            quantity = 8
        )
    )
}