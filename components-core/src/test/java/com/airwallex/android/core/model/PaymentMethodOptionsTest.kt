package com.airwallex.android.core.model

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentMethodOptionsTest {

    private val paymentMethodOptions by lazy {
        PaymentMethodOptions.Builder()
            .setCardOptions(
                PaymentMethodOptions.CardOptions.Builder()
                    .setAutoCapture(true)
                    .setThreeDSecure(
                        ThreeDSecure.Builder()
                            .setReturnUrl("https://www.360safe.com")
                            .setDeviceDataCollectionRes("abc")
                            .setTransactionId("123")
                            .build()
                    )
                    .build()
            )
            .build()
    }

    @Test
    fun builderConstructor() {
        assertEquals(paymentMethodOptions, PaymentMethodOptionsFixtures.PAYMENTMETHODOPTIONS)
    }

    @Test
    fun testParams() {
        assertEquals(
            PaymentMethodOptions.CardOptions.Builder()
                .setAutoCapture(true)
                .setThreeDSecure(
                    ThreeDSecure.Builder()
                        .setReturnUrl("https://www.360safe.com")
                        .setDeviceDataCollectionRes("abc")
                        .setTransactionId("123")
                        .build()
                )
                .build(),
            paymentMethodOptions.cardOptions
        )
    }
}
