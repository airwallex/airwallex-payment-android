package com.airwallex.android.view

import com.airwallex.android.R
import org.junit.Test
import kotlin.test.assertEquals

class CardBrandTest {

    @Test
    fun `test card brands`() {
        assertEquals("visa", CardBrand.Visa.type)
        assertEquals(R.drawable.airwallex_ic_visa, CardBrand.Visa.icon)
        assertEquals(listOf(4, 4, 4, 4), CardBrand.Visa.spacingPattern)

        assertEquals("mastercard", CardBrand.MasterCard.type)
        assertEquals(R.drawable.airwallex_ic_mastercard, CardBrand.MasterCard.icon)
        assertEquals(listOf(4, 4, 4, 4), CardBrand.MasterCard.spacingPattern)

        assertEquals("amex", CardBrand.Amex.type)
        assertEquals(R.drawable.airwallex_ic_amex, CardBrand.Amex.icon)
        assertEquals(listOf(4, 6, 5), CardBrand.Amex.spacingPattern)

        assertEquals("unionpay", CardBrand.UnionPay.type)
        assertEquals(R.drawable.airwallex_ic_unionpay, CardBrand.UnionPay.icon)
        assertEquals(listOf(4, 4, 4, 4), CardBrand.UnionPay.spacingPattern)

        assertEquals("jcb", CardBrand.JCB.type)
        assertEquals(R.drawable.airwallex_ic_jcb, CardBrand.JCB.icon)
        assertEquals(listOf(4, 4, 4, 4), CardBrand.JCB.spacingPattern)

        assertEquals("unknown", CardBrand.Unknown.type)
        assertEquals(R.drawable.airwallex_ic_card_default, CardBrand.Unknown.icon)
        assertEquals(listOf(4, 4, 4, 4), CardBrand.Unknown.spacingPattern)
    }

    @Test
    fun `test fromType`() {
        assertEquals(CardBrand.fromType("mastercard"), CardBrand.MasterCard)
    }

    @Test
    fun `test fromName`() {
        assertEquals(CardBrand.fromName("mastercard"), CardBrand.MasterCard)
        assertEquals(CardBrand.fromName("american express"), CardBrand.Amex)
        assertEquals(CardBrand.fromName("union pay"), CardBrand.UnionPay)
    }
}
