package com.airwallex.android.view

import com.airwallex.android.R
import org.junit.Test
import kotlin.test.assertEquals

class CardBrandTest {

    @Test
    fun cardBrandTest() {
        assertEquals("visa", CardBrand.Visa.type)
        assertEquals(R.drawable.airwallex_ic_visa, CardBrand.Visa.icon)

        assertEquals("mastercard", CardBrand.MasterCard.type)
        assertEquals(R.drawable.airwallex_ic_mastercard, CardBrand.MasterCard.icon)

        assertEquals("unknown", CardBrand.Unknown.type)
        assertEquals(R.drawable.airwallex_ic_card_default, CardBrand.Unknown.icon)
    }
}
