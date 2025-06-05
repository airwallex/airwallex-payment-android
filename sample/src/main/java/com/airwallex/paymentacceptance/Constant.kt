package com.airwallex.paymentacceptance

import com.airwallex.android.core.AirwallexCheckoutMode
import com.airwallex.android.core.Environment
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentMethod
import com.airwallex.android.core.model.PhysicalProduct
import com.airwallex.android.core.model.Shipping

val products = listOf(
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

val force3DS: Boolean
    get() {
        return when (Settings.force3DS) {
            SampleApplication.instance.resources.getStringArray(R.array.array_force_3ds)[0] -> false
            SampleApplication.instance.resources.getStringArray(R.array.array_force_3ds)[1] -> true
            else -> throw Exception("Unsupported force3ds: ${Settings.force3DS}")
        }
    }

val autoCapture: Boolean
    get() {
        return when (Settings.autoCapture) {
            SampleApplication.instance.resources.getStringArray(R.array.array_auto_capture)[0] -> true
            SampleApplication.instance.resources.getStringArray(R.array.array_auto_capture)[1] -> false
            else -> throw Exception("Unsupported autoCapture: ${Settings.autoCapture}")
        }
    }

val card: PaymentMethod.Card
    get() {
        val stagingCardNumber = when (Settings.checkoutMode) {
            AirwallexCheckoutMode.PAYMENT -> "4012000300000005"
            AirwallexCheckoutMode.RECURRING -> "4012000300000021"
            AirwallexCheckoutMode.RECURRING_WITH_INTENT -> "4012000300000005"
        }
        val demoCardNumber = when (Settings.checkoutMode) {
            AirwallexCheckoutMode.PAYMENT -> "4012000300001003"
            AirwallexCheckoutMode.RECURRING -> "4035501000000008"
            AirwallexCheckoutMode.RECURRING_WITH_INTENT -> "4012000300001003"
        }
        return PaymentMethod.Card.Builder()
            .setNumber(if (Settings.getEnvironment() == Environment.STAGING) stagingCardNumber else demoCardNumber)
            .setName("John Citizen")
            .setExpiryMonth("12")
            .setExpiryYear("2029")
            .setCvc("737")
            .build()
    }

val card3DS: PaymentMethod.Card
    get() {
        val stagingCardNumber = when (Settings.checkoutMode) {
            AirwallexCheckoutMode.PAYMENT -> "4012000300000088"
            AirwallexCheckoutMode.RECURRING -> "5425233430109903"
            AirwallexCheckoutMode.RECURRING_WITH_INTENT -> "4012000300000088"
        }
        val demoCardNumber = when (Settings.checkoutMode) {
            AirwallexCheckoutMode.PAYMENT -> "4012000300000088"
            AirwallexCheckoutMode.RECURRING -> "5307837360544518"
            AirwallexCheckoutMode.RECURRING_WITH_INTENT -> "4012000300000088"
        }
        return PaymentMethod.Card.Builder()
            .setNumber(if (Settings.getEnvironment() == Environment.STAGING) stagingCardNumber else demoCardNumber)
            .setName("John Citizen")
            .setExpiryMonth("12")
            .setExpiryYear("2029")
            .setCvc("737")
            .build()
    }
