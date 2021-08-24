package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntentFixtures
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class AirwallexPaymentSessionTest {

    @Test
    fun buildTest() {
        val airwallexPaymentSession = AirwallexPaymentSession.Builder(PaymentIntentFixtures.PAYMENT_INTENT).build()

        assertNotNull(airwallexPaymentSession)

        assertNotNull(airwallexPaymentSession.currency)
        assertEquals("AUD", airwallexPaymentSession.currency)

        assertNotNull(airwallexPaymentSession.amount)
        assertEquals(BigDecimal.valueOf(100.01), airwallexPaymentSession.amount)

        assertEquals(null, airwallexPaymentSession.shipping)

        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", airwallexPaymentSession.customerId)
    }
}