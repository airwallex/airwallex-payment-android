package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentConsent
import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class AirwallexRecurringSessionTest {

    @Test
    fun buildTest() {
        val airwallexRecurringSession = AirwallexRecurringSession.Builder(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            currency = "AUD",
            amount = BigDecimal.valueOf(100.01),
            countryCode = "CN"
        )
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setRequireBillingInformation(false)
            .setRequireCvc(true)
            .setRequireEmail(true)
            .setReturnUrl("airwallexcheckout://com.airwallex.paymentacceptance")
            .setPaymentMethods(listOf("googlepay"))
            .build()

        assertNotNull(airwallexRecurringSession)

        assertNotNull(airwallexRecurringSession.currency)
        assertEquals("AUD", airwallexRecurringSession.currency)

        assertNotNull(airwallexRecurringSession.amount)
        assertEquals(BigDecimal.valueOf(100.01), airwallexRecurringSession.amount)

        assertNotNull(airwallexRecurringSession.nextTriggerBy)
        assertEquals(PaymentConsent.NextTriggeredBy.CUSTOMER, airwallexRecurringSession.nextTriggerBy)

        assertNotNull(airwallexRecurringSession.requiresCVC)
        assertEquals(true, airwallexRecurringSession.requiresCVC)

        assertNotNull(airwallexRecurringSession.isBillingInformationRequired)
        assertEquals(false, airwallexRecurringSession.isBillingInformationRequired)

        assertTrue(airwallexRecurringSession.isEmailRequired)

        assertEquals(null, airwallexRecurringSession.shipping)

        assertNotNull(airwallexRecurringSession.customerId)
        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", airwallexRecurringSession.customerId)

        assertEquals("airwallexcheckout://com.airwallex.paymentacceptance", airwallexRecurringSession.returnUrl)
        assertNotNull(airwallexRecurringSession.paymentMethods)
    }
}
