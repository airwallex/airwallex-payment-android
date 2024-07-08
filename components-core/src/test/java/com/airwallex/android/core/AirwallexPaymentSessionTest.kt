package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentIntentFixtures
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AirwallexPaymentSessionTest {

    @Test
    fun buildTest() {
        val airwallexPaymentSession = AirwallexPaymentSession.Builder(
            PaymentIntentFixtures.PAYMENT_INTENT, "CN"
        )
            .setRequireBillingInformation(false)
            .setRequireEmail(true)
            .setReturnUrl("airwallexcheckout://com.airwallex.paymentacceptance")
            .setPaymentMethods(listOf("googlepay"))
            .setHidePaymentConsents(false)
            .setAutoCapture(false)
            .build()

        assertNotNull(airwallexPaymentSession)

        assertNotNull(airwallexPaymentSession.currency)
        assertEquals("AUD", airwallexPaymentSession.currency)

        assertNotNull(airwallexPaymentSession.amount)
        assertEquals(BigDecimal.valueOf(100.01), airwallexPaymentSession.amount)

        assertNotNull(airwallexPaymentSession.isBillingInformationRequired)
        assertEquals(false, airwallexPaymentSession.isBillingInformationRequired)

        assertTrue(airwallexPaymentSession.isEmailRequired)

        assertEquals(null, airwallexPaymentSession.shipping)

        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", airwallexPaymentSession.customerId)
        assertEquals("airwallexcheckout://com.airwallex.paymentacceptance", airwallexPaymentSession.returnUrl)
        assertEquals(false, airwallexPaymentSession.autoCapture)
        assertEquals(false, airwallexPaymentSession.hidePaymentConsents)
        assertNotNull(airwallexPaymentSession.paymentMethods)
    }
}
