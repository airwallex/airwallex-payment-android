package com.airwallex.android.core

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class PaymentAppearanceTest {

    @Test
    fun `test PaymentAppearance creation`() {
        val appearance = PaymentAppearance(
            themeColor = 0xFF612FFF.toInt(),
            isDarkTheme = true
        )

        assertEquals(0xFF612FFF.toInt(), appearance.themeColor)
        assertEquals(true, appearance.isDarkTheme)
    }

    @Test
    fun `test PaymentAppearance default constructor`() {
        val appearance = PaymentAppearance()

        assertNull(appearance.themeColor)
        assertNull(appearance.isDarkTheme)
    }
}
