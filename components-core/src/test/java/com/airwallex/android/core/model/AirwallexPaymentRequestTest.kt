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
        assertEquals(
            AirwallexPaymentRequestFixtures.REQUEST,
            ParcelUtils.create(AirwallexPaymentRequestFixtures.REQUEST)
        )
    }

    @Test
    fun testParams() {
        val airwallexPaymentRequest = AirwallexPaymentRequestFixtures.REQUEST
        assertEquals("CN", airwallexPaymentRequest.countryCode)
        assertEquals("cstore@163.com", airwallexPaymentRequest.email)
        assertEquals("cstore", airwallexPaymentRequest.name)
        assertEquals(Bank.KRUNGSRI, airwallexPaymentRequest.bank)
        assertEquals("18833332222", airwallexPaymentRequest.phone)
        assertEquals(AirwallexPaymentRequestFlow.IN_APP, airwallexPaymentRequest.flow)
        assertEquals("android", airwallexPaymentRequest.osType)
    }
}
