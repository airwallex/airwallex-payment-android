package com.airwallex.android.core.model

import org.junit.Test
import kotlin.test.assertEquals

class CardSchemeTest {
    private val cardScheme by lazy {
        CardScheme(
            "Visa"
        )
    }

    @Test
    fun testParams() {
        assertEquals(cardScheme.name, "Visa")
    }
}
