package com.airwallex.android.view

import com.airwallex.android.view.util.CardUtils
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CardUtilsTest {

    @Test
    fun `test getPossibleCardBrand`() {
        assertEquals(CardBrand.Visa, CardUtils.getPossibleCardBrand("4242424242424242", false))
        assertEquals(CardBrand.MasterCard, CardUtils.getPossibleCardBrand("5555555555554444", false))
        assertEquals(CardBrand.Amex, CardUtils.getPossibleCardBrand("3782 8224 6310 005", true))
        assertEquals(CardBrand.UnionPay, CardUtils.getPossibleCardBrand("6212 3188 8888 8888 888", true))
        assertEquals(CardBrand.JCB, CardUtils.getPossibleCardBrand("3540 1234 5678 9012", true))
        assertEquals(CardBrand.DISCOVER, CardUtils.getPossibleCardBrand("6011016011016011", false))
        assertEquals(CardBrand.DINERS, CardUtils.getPossibleCardBrand("36438936438936", false))
        assertEquals(CardBrand.Unknown, CardUtils.getPossibleCardBrand("12345", false))
    }

    @Test
    fun `test isValidCardLength when Visa card number`() {
        assertTrue(CardUtils.isValidCardLength("4242424242424242"))
        assertFalse(CardUtils.isValidCardLength("42424242424242"))
        assertFalse(CardUtils.isValidCardLength(null))
    }

    @Test
    fun `test isValidCardLength when Amex card number`() {
        assertTrue(CardUtils.isValidCardLength("3782 8224 6310 005", true))
        assertFalse(CardUtils.isValidCardLength("3782822463100055"))
    }

    @Test
    fun `test isValidCardLength when UnionPay card number`() {
        assertTrue(CardUtils.isValidCardLength("6212 3188 8888 8888 88", true))
        assertFalse(CardUtils.isValidCardLength("621231888888888"))
    }

    @Test
    fun `test isValidCardLength when not a valid card brand`() {
        assertFalse(CardUtils.isValidCardLength("1234567890123456"))
    }

    @Test
    fun `test isValidLuhnNumber`() {
        assertTrue(CardUtils.isValidLuhnNumber("4242424242424242"))
        assertFalse(CardUtils.isValidLuhnNumber("4242424242424244"))
        assertFalse(CardUtils.isValidLuhnNumber(null))
    }

    @Test
    fun `test getSpacePositions`() {
        assertEquals(CardUtils.getSpacePositions(CardBrand.MasterCard), setOf(4, 9, 14))
        assertEquals(CardUtils.getSpacePositions(CardBrand.Amex), setOf(4, 11))
    }

    @Test
    fun `test maxCardNumberLength`() {
        assertEquals(CardUtils.maxCardNumberLength, 19)
    }
}
