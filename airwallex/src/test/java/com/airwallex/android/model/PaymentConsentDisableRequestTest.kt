package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentConsentDisableRequestTest {
    @Test
    fun builderConstructor() {
        val paymentConsentDisableRequest = PaymentConsentDisableRequest.Builder()
            .setRequestId("abc")
            .build()
        assertEquals(paymentConsentDisableRequest, PaymentConsentDisableRequestFixtures.PCDR)
    }
    @Test
    fun testParcelable() {
        assertEquals(
            PaymentConsentDisableRequestFixtures.PCDR,
            ParcelUtils.create(PaymentConsentDisableRequestFixtures.PCDR)
        )
    }
}
