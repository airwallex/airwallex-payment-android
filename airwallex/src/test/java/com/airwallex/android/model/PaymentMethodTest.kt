package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentMethodTest {

    @Test
    fun builderConstructor() {
        val paymentMethod = PaymentMethod.Builder()
            .setId("")
            .setRequestId("")
            .setType(PaymentMethodType.CARD)
            .setCard(
                PaymentMethod.Card.Builder()
                    .setNumber("4012000300001003")
                    .setExpiryMonth("12")
                    .setExpiryYear("2020")
                    .setCvc("123")
                    .setName("Adam")
                    .build()
            )
            .setBilling(
                Billing.Builder()
                    .setFirstName("Jim")
                    .setLastName("He")
                    .setPhone("1367875786")
                    .setAddress(
                        Address.Builder()
                            .setCountryCode("CN")
                            .setState("Shanghai")
                            .setCity("Shanghai")
                            .setStreet("Pudong District")
                            .setPostcode("201304")
                            .build()
                    )
                    .build()
            )
            .build()
        assertEquals(paymentMethod, PaymentMethodFixtures.PAYMENT_METHOD)
    }

    @Test
    fun testParcelable() {
        assertEquals(
            PaymentMethodFixtures.PAYMENT_METHOD,
            ParcelUtils.create(PaymentMethodFixtures.PAYMENT_METHOD)
        )
    }
}
