package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentMethodCreatRequestTest {

    @Test
    fun builderConstructor() {
        val paymentMethodCreatRequest = PaymentMethodCreateRequest.Builder()
            .setRequestId("1")
            .setCustomerId("2")
            .setCard(
                PaymentMethod.Card.Builder()
                    .setExpiryMonth("12")
                    .setExpiryYear("2020")
                    .setName("Adam")
                    .setNumber("4012000300001003")
                    .setCvc("123")
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
            .setType(PaymentMethodType.CARD)
            .build()
        assertEquals(paymentMethodCreatRequest, PaymentMethodCreatRequestFixtures.PMMCR)
    }

    @Test
    fun testParcelable() {
        assertEquals(
            PaymentMethodCreatRequestFixtures.PMMCR,
            ParcelUtils.create(PaymentMethodCreatRequestFixtures.PMMCR)
        )
    }
}
