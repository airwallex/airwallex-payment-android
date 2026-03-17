package com.airwallex.android.core

import com.airwallex.android.core.log.AnalyticsLogger.Field
import org.junit.Test
import kotlin.test.assertEquals

class AirwallexPaymentStatusTests {
    @Test
    fun `test success status`() {
        val status =
            AirwallexPaymentStatus.Success("id", "consentId", mapOf(Field.PAYMENT_METHOD to "googlepay"))
        assertEquals(status.paymentIntentId, "id")
        assertEquals(status.additionalInfo?.get(Field.PAYMENT_METHOD), "googlepay")
    }
}
