package com.airwallex.android.model

import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentMethodOptionsTest {

    @Test
    fun builderConstructor() {
        val paymentMethodOptions = PaymentMethodOptions.Builder()
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
        assertEquals(paymentMethodOptions, PaymentMethodOptionsFixtures.PAYMENTMETHODOPTIONS)
    }
}
