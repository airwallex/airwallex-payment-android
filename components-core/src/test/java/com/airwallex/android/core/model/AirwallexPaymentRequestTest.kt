package com.airwallex.android.core.model

import com.airwallex.android.core.ParcelUtils
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import kotlin.test.assertEquals

@RunWith(RobolectricTestRunner::class)
class AirwallexPaymentRequestTest {

    @Test
    fun testParcelable() {
        assertEquals(AirwallexPaymentRequestFixtures.REQUEST, ParcelUtils.create(AirwallexPaymentRequestFixtures.REQUEST))
    }
}
