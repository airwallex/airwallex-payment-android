package com.airwallex.android.core

import com.airwallex.android.core.model.PaymentConsent
import com.airwallex.android.core.model.PaymentIntentFixtures
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import java.math.BigDecimal
import kotlin.test.assertEquals
import kotlin.test.assertNotNull

@RunWith(RobolectricTestRunner::class)
class AirwallexRecurringWithIntentSessionTest {

    @Test
    fun buildTest() {
        val airwallexRecurringWithIntentSession = AirwallexRecurringWithIntentSession.Builder(
            nextTriggerBy = PaymentConsent.NextTriggeredBy.CUSTOMER,
            customerId = "cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu",
            paymentIntent = PaymentIntentFixtures.PAYMENT_INTENT,
            countryCode = "CN"
        )
            .setMerchantTriggerReason(PaymentConsent.MerchantTriggerReason.SCHEDULED)
            .setRequireCvc(true)
            .build()

        assertNotNull(airwallexRecurringWithIntentSession)

        assertNotNull(airwallexRecurringWithIntentSession.currency)
        assertEquals("AUD", airwallexRecurringWithIntentSession.currency)

        assertNotNull(airwallexRecurringWithIntentSession.amount)
        assertEquals(BigDecimal.valueOf(100.01), airwallexRecurringWithIntentSession.amount)

        assertNotNull(airwallexRecurringWithIntentSession.nextTriggerBy)
        assertEquals(
            PaymentConsent.NextTriggeredBy.CUSTOMER,
            airwallexRecurringWithIntentSession.nextTriggerBy
        )

        assertNotNull(airwallexRecurringWithIntentSession.requiresCVC)
        assertEquals(true, airwallexRecurringWithIntentSession.requiresCVC)

        assertEquals(null, airwallexRecurringWithIntentSession.shipping)

        assertNotNull(airwallexRecurringWithIntentSession.customerId)
        assertEquals("cus_ps8e0ZgQzd2QnCxVpzJrHD6KOVu", airwallexRecurringWithIntentSession.customerId)
    }
}
