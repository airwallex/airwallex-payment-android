package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentConsentVerifyRequestTest {
    @Test
    fun builderConstructor() {
        val paymentConsentVerifyRequest = PaymentConsentVerifyRequest.Builder()
            .setRequestId("abc")
            .setReturnUrl("https://www.airwallex.com")
            .setVerificationOptions(null)
            .build()
        assertEquals(paymentConsentVerifyRequest, PaymentConsentVerifyRequestFixtures.PCVR)
    }
    @Test
    fun testParcelable() {
        assertEquals(
            PaymentConsentVerifyRequestFixtures.PCVR,
            ParcelUtils.create(PaymentConsentVerifyRequestFixtures.PCVR)
        )
    }
}
