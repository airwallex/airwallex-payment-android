package com.airwallex.android.view

import com.airwallex.android.R
import org.junit.Test
import kotlin.test.assertEquals

class CardBrandTest {

    @Test
    fun `test card brands`() {
        assertEquals("visa", CardBrand.Visa.type)
        assertEquals(R.drawable.airwallex_ic_visa, CardBrand.Visa.icon)

        assertEquals("mastercard", CardBrand.MasterCard.type)
        assertEquals(R.drawable.airwallex_ic_mastercard, CardBrand.MasterCard.icon)

        assertEquals("amex", CardBrand.Amex.type)
        assertEquals(R.drawable.airwallex_ic_amex, CardBrand.Amex.icon)

        assertEquals("unknown", CardBrand.Unknown.type)
        assertEquals(R.drawable.airwallex_ic_card_default, CardBrand.Unknown.icon)
    }

    @Test
    fun `test fromType`() {
        assertEquals(CardBrand.fromType("mastercard"), CardBrand.MasterCard)
    }
}
