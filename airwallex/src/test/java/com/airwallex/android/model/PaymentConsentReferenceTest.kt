package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentConsentReferenceTest {
    @Test
    fun builderConstructor() {
        val paymentConsentReference = PaymentConsentReference.Builder()
            .setId("1")
            .setCvc("123")
            .build()
        assertEquals(paymentConsentReference, PaymentConsentReferenceFixtures.PCR)
    }
    @Test
    fun testParcelable() {
        assertEquals(
            PaymentConsentReferenceFixtures.PCR,
            ParcelUtils.create(PaymentConsentReferenceFixtures.PCR)
        )
    }
}
