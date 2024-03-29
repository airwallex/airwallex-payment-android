package com.airwallex.android.core.model

import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentMethodOptionsTest {

    private val cardOptions by lazy {
        PaymentMethodOptions.CardOptions.Builder()
            .setAutoCapture(true)
            .setThreeDSecure(
                ThreeDSecure.Builder()
                    .setReturnUrl("https://www.360safe.com")
                    .build()
            )
            .build()
    }

    private val paymentMethodOptions by lazy {
        PaymentMethodOptions.Builder()
            .setCardOptions(
                cardOptions
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
                        .build()
                )
                .build(),
            paymentMethodOptions.cardOptions
        )
    }

    @Test
    fun testToParamsMap() {
        val cardParamMap = cardOptions.toParamMap()
        assertEquals(
            mapOf(
                "auto_capture" to true,
                "three_ds" to mapOf(
                    "return_url" to "https://www.360safe.com"
                )
            ),
            cardParamMap
        )

        val paramMap = paymentMethodOptions.toParamMap()
        assertEquals(
            mapOf(
                "card" to mapOf(
                    "auto_capture" to true,
                    "three_ds" to mapOf(
                        "return_url" to "https://www.360safe.com"
                    )
                )
            ),
            paramMap
        )
    }
}
