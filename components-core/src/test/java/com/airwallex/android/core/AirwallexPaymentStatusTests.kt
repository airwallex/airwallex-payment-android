package com.airwallex.android.core

import org.junit.Test
import kotlin.test.assertEquals

class AirwallexPaymentStatusTests {
    @Test
    fun `test success status`() {
        val status = AirwallexPaymentStatus.Success("id", mapOf("paymentMethod" to "googlepay"))
        assertEquals(status.paymentIntentId, "id")
        assertEquals(status.additionalInfo?.get("paymentMethod"), "googlepay")
    }
}
