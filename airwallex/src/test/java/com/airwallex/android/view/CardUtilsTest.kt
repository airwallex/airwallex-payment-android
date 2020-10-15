package com.airwallex.android.view

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CardUtilsTest {

    @Test
    fun getPossibleCardBrandTest() {
        assertEquals(CardBrand.Visa, CardUtils.getPossibleCardBrand("4242424242424242", false))
        assertEquals(CardBrand.MasterCard, CardUtils.getPossibleCardBrand("5555555555554444", false))
    }

    @Test
    fun isValidCardLengthTest() {
        assertTrue(CardUtils.isValidCardLength("4242424242424242"))
        assertFalse(CardUtils.isValidCardLength("42424242424242"))
        assertFalse(CardUtils.isValidCardLength(null))
    }

    @Test
    fun isValidLuhnNumberTest() {
        assertTrue(CardUtils.isValidLuhnNumber("4242424242424242"))
        assertFalse(CardUtils.isValidLuhnNumber("4242424242424244"))
        assertFalse(CardUtils.isValidLuhnNumber(null))
    }
}
