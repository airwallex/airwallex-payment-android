package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentMethodTest {

    private val paymentMethod by lazy {
        PaymentMethod.Builder()
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
    }

    @Test
    fun builderConstructor() {
        assertEquals(paymentMethod, PaymentMethodFixtures.PAYMENT_METHOD)
    }

    @Test
    fun testParcelable() {
        assertEquals(
            PaymentMethodFixtures.PAYMENT_METHOD,
            ParcelUtils.create(PaymentMethodFixtures.PAYMENT_METHOD)
        )
    }

    @Test
    fun testParams() {
        assertEquals(PaymentMethodType.CARD, paymentMethod.type)
        assertEquals(
            PaymentMethod.Card.Builder()
                .setNumber("4012000300001003")
                .setExpiryMonth("12")
                .setExpiryYear("2020")
                .setCvc("123")
                .setName("Adam")
                .build(),
            paymentMethod.card
        )
        assertEquals(
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
                .build(),
            paymentMethod.billing
        )
    }
}
