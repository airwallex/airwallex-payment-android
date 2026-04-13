package com.airwallex.android.core

import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class AppearanceTest {

    @Test
    fun `test PaymentAppearance creation`() {
        val appearance = Appearance(
            themeColor = 0xFF612FFF.toInt(),
            isDarkTheme = true
        )

        assertEquals(0xFF612FFF.toInt(), appearance.themeColor)
        assertEquals(true, appearance.isDarkTheme)
    }

    @Test
    fun `test PaymentAppearance default constructor`() {
        val appearance = Appearance()

        assertNull(appearance.themeColor)
        assertNull(appearance.isDarkTheme)
    }
}
