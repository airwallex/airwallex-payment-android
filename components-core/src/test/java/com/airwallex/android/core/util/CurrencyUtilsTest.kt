package com.airwallex.android.core.util

import org.junit.Test
import java.math.BigDecimal
import kotlin.test.assertEquals

class CurrencyUtilsTest {

    @Test
    fun getCurrencySymbol() {
        assertEquals("HK$", CurrencyUtils.getCurrencySymbol("HKD"))
    }

    @Test
    fun formatPriceTest() {
        assertEquals("$1.00", CurrencyUtils.formatPrice("HKD", BigDecimal.ONE))
    }
}
