package com.airwallex.android.core

import com.airwallex.android.core.log.AnalyticsLogger
import org.junit.Test
import kotlin.test.assertEquals

class PaymentMethodsLayoutTypeTest {

    @Test
    fun `toAnalyticsLayoutString returns tab for TAB layout`() {
        val layoutType = PaymentMethodsLayoutType.TAB
        val result = layoutType.toAnalyticsLayoutString()
        assertEquals(AnalyticsLogger.Layout.TAB, result)
        assertEquals("tab", result)
    }

    @Test
    fun `toAnalyticsLayoutString returns accordion for ACCORDION layout`() {
        val layoutType = PaymentMethodsLayoutType.ACCORDION
        val result = layoutType.toAnalyticsLayoutString()
        assertEquals(AnalyticsLogger.Layout.ACCORDION, result)
        assertEquals("accordion", result)
    }
}
