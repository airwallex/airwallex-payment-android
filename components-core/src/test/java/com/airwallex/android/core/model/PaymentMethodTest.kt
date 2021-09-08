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

    @Test
    fun testCardParams() {
        val card = PaymentMethod.Card.Builder()
            .setNumber("4012000300001003")
            .setExpiryMonth("12")
            .setExpiryYear("2020")
            .setCvc("123")
            .setName("Adam")
            .build()

        assertEquals("123", card.cvc)
        assertEquals("12", card.expiryMonth)
        assertEquals("2020", card.expiryYear)
        assertEquals("Adam", card.name)
        assertEquals("4012000300001003", card.number)
        assertEquals(null, card.bin)
        assertEquals(null, card.last4)
        assertEquals(null, card.brand)
        assertEquals(null, card.country)
        assertEquals(null, card.funding)
        assertEquals(null, card.fingerprint)
        assertEquals(null, card.cvcCheck)
        assertEquals(null, card.avsCheck)
        assertEquals(null, card.issuerCountryCode)
        assertEquals(null, card.cardType)
    }

    @Test
    fun testCardToParamsMap() {
        val cardParamMap = PaymentMethod.Card.Builder()
            .setNumber("4012000300001003")
            .build()
            .toParamMap()

        assertEquals(mapOf("number" to "4012000300001003"), cardParamMap)
    }
}
