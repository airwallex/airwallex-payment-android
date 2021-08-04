package com.airwallex.android.model

import com.airwallex.android.ParcelUtils
import kotlin.test.assertEquals
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class PaymentMethodDisableRequestTest {

    @Test
    fun builderConstructor() {
        val paymentMethodCreatRequest = PaymentMethodDisableRequest.Builder()
            .setRequestId("1")
            .build()
        assertEquals(paymentMethodCreatRequest, PaymentMethodDisableRequestFixtures.PMDR)
    }

    @Test
    fun testParcelable() {
        assertEquals(
            PaymentMethodDisableRequestFixtures.PMDR,
            ParcelUtils.create(PaymentMethodDisableRequestFixtures.PMDR)
        )
    }
}
