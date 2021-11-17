package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentConsent
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
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
            .setRequireCvc(true)
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

        assertEquals(null, airwallexRecurringSession.shipping)

        assertNotNull(airwallexRecurringSession.customerId)
        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", airwallexRecurringSession.customerId)
    }
}
