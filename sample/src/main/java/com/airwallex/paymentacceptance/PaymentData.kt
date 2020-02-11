package com.airwallex.paymentacceptance

import com.airwallex.android.model.*

object PaymentData {

    val device = Device.Builder()
        .setBrowserInfo("Chrome/76.0.3809.100")
        .setCookiesAccepted("true")
        .setDeviceId("IMEI-4432fsdafd31243244fdsafdfd653")
        .setHostName("www.airwallex.com")
        .setHttpBrowserEmail("jim631@sina.com")
        .setHttpBrowserType("chrome")
        .setIpAddress("123.90.0.1")
        .setIpNetworkAddress("128.0.0.0")
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
}