package com.airwallex.paymentacceptance

import android.media.audiofx.DynamicsProcessing.Stage
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PhysicalProduct
import com.airwallex.android.core.model.Shipping

val products = mutableListOf(
    PhysicalProduct.Builder()
        .setCode("123")
        .setName("AirPods")
        .setDesc("Buy AirPods Pro, per month with trade-in")
        .setSku("piece")
        .setType("White")
        .setUnitPrice(500.00)
        .setUrl("www.aircross.com")
        .setQuantity(1)
        .build(),
    PhysicalProduct.Builder()
        .setCode("123")
        .setName("HomePod")
        .setDesc("Buy HomePod, per month with trade-in")
        .setSku("piece")
        .setType("White")
        .setUnitPrice(500.00)
        .setUrl("www.aircross.com")
        .setQuantity(1)
        .build()
)

val shipping = Shipping.Builder()
    .setFirstName("Doe")
    .setLastName("John")
    .setPhone("13800000000")
    .setEmail("john.doe@airwallex.com")
    .setAddress(
        Address.Builder()
            .setCountryCode("CN")
            .setState("Shanghai")
            .setCity("Shanghai")
            .setStreet("Julu road")
            .setPostcode("100000")
            .build()
    )
    .build()

val nextTriggerBy: PaymentConsent.NextTriggeredBy
    get() {
        return when (Settings.nextTriggerBy) {
            SampleApplication.instance.resources.getStringArray(R.array.array_next_trigger_by)[0] -> PaymentConsent.NextTriggeredBy.MERCHANT
            SampleApplication.instance.resources.getStringArray(R.array.array_next_trigger_by)[1] -> PaymentConsent.NextTriggeredBy.CUSTOMER
            else -> throw Exception("Unsupported NextTriggerBy: ${Settings.nextTriggerBy}")
        }
    }


var stagingCard = PaymentMethod.Card.Builder()
    .setNumber("4012000300000005")
    .setName("John Citizen")
    .setExpiryMonth("12")
    .setExpiryYear("2029")
    .setCvc("737")
    .build()

var stagingCard3DS = PaymentMethod.Card.Builder()
    .setNumber("4012000300000088")
    .setName("John Citizen")
    .setExpiryMonth("12")
    .setExpiryYear("2029")
    .setCvc("737")
    .build()

var demoCard = PaymentMethod.Card.Builder()
    .setNumber("4012000300001003")
    .setName("John Citizen")
    .setExpiryMonth("12")
    .setExpiryYear("2029")
    .setCvc("737")
    .build()

var demoCard3DS = PaymentMethod.Card.Builder()
    .setNumber("4012000300000021")
    .setName("John Citizen")
    .setExpiryMonth("12")
    .setExpiryYear("2029")
    .setCvc("737")
    .build()
