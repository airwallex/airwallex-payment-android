package com.airwallex.paymentacceptance

import android.app.Application
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexConfiguration
import com.airwallex.android.model.Address
import com.airwallex.android.model.Device
import com.airwallex.android.model.Product
import com.airwallex.android.model.Shipping

class SampleApplication : Application() {

    companion object {
        lateinit var instance: SampleApplication
    }

    override fun onCreate() {
        super.onCreate()

        instance = this

        Airwallex.initialize(
            AirwallexConfiguration.Builder(this)
                .enableLogging(true)
                .build()
        )
    }

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

    val products = mutableListOf(
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

    var shipping: Shipping = Shipping.Builder()
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