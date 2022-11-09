package com.airwallex.android.view

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
    fun `test isValidCardLength when not a valid card brand`() {
        assertFalse(CardUtils.isValidCardLength("1234567890123456"))
    }

    @Test
    fun `test isValidLuhnNumber`() {
        assertTrue(CardUtils.isValidLuhnNumber("4242424242424242"))
        assertFalse(CardUtils.isValidLuhnNumber("4242424242424244"))
        assertFalse(CardUtils.isValidLuhnNumber(null))
    }
}
