package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class PaymentConsentTest {

    @Test
    fun testParcelable() {
        assertEquals(
            PaymentConsentFixtures.PAYMENTCONSENT,
            ParcelUtils.create(PaymentConsentFixtures.PAYMENTCONSENT)
        )
    }
}
