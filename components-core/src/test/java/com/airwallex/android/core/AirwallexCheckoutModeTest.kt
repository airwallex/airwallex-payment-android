package com.airwallex.android.core

import org.junit.Test
import kotlin.test.assertEquals

class AirwallexCheckoutModeTest {

    @Test
    fun testCheckoutMode() {
        assertEquals("PAYMENT", AirwallexCheckoutMode.PAYMENT.name)
        assertEquals("RECURRING", AirwallexCheckoutMode.RECURRING.name)
        assertEquals("RECURRING_WITH_INTENT", AirwallexCheckoutMode.RECURRING_WITH_INTENT.name)
    }
}
