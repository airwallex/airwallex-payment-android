package com.airwallex.android.core.util

import com.airwallex.android.core.CardBrand
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

    @Test
    fun `test formatCardNumber for Visa`() {
        // Test full length
        assertEquals("4242 4242 4242 4242", CardUtils.formatCardNumber("4242424242424242", CardBrand.Visa))
        // Test partial number
        assertEquals("4242 42", CardUtils.formatCardNumber("424242", CardBrand.Visa))
        // Test with spaces in input
        assertEquals("4242 4242 4242 4242", CardUtils.formatCardNumber("4242 4242 4242 4242", CardBrand.Visa))
        // Test empty string
        assertEquals("", CardUtils.formatCardNumber("", CardBrand.Visa))
    }

    @Test
    fun `test formatCardNumber for Amex`() {
        // Test full length
        assertEquals("3782 822463 10005", CardUtils.formatCardNumber("378282246310005", CardBrand.Amex))
        // Test partial number (before first space)
        assertEquals("3782", CardUtils.formatCardNumber("3782", CardBrand.Amex))
        // Test partial number (after first space, before second space)
        assertEquals("3782 822463", CardUtils.formatCardNumber("3782822463", CardBrand.Amex))
    }

    @Test
    fun `test formatCardNumber for MasterCard`() {
        // Test full length
        assertEquals("5555 5555 5555 4444", CardUtils.formatCardNumber("5555555555554444", CardBrand.MasterCard))
        // Test partial number
        assertEquals("5555 5555 55", CardUtils.formatCardNumber("5555555555", CardBrand.MasterCard))
    }

    @Test
    fun `test formatCardNumber for UnionPay`() {
        // Test full length (19 digits)
        assertEquals("6212 3188 8888 8888888", CardUtils.formatCardNumber("6212318888888888888", CardBrand.UnionPay))
        // Test partial number
        assertEquals("6212 3188 88", CardUtils.formatCardNumber("6212318888", CardBrand.UnionPay))
    }

    @Test
    fun `test formatCardNumber for JCB`() {
        // Test full length
        assertEquals("3540 1234 5678 9012", CardUtils.formatCardNumber("3540123456789012", CardBrand.JCB))
    }

    @Test
    fun `test formatCardNumber for Discover`() {
        // Test full length
        assertEquals("6011 6011 6011 6611", CardUtils.formatCardNumber("6011601160116611", CardBrand.DISCOVER))
    }

    @Test
    fun `test formatCardNumber for Diners`() {
        // Test full length (14 digits)
        assertEquals("3643 893643 8936", CardUtils.formatCardNumber("36438936438936", CardBrand.DINERS))
    }

    @Test
    fun `test isValidCardNumber with valid card`() {
        assertTrue(CardUtils.isValidCardNumber("4242424242424242"))
    }

    @Test
    fun `test isValidCardNumber with invalid card`() {
        assertFalse(CardUtils.isValidCardNumber("4242424242424244"))
    }

    @Test
    fun `test isValidCardNumber with card containing digits that double to greater than 9`() {
        // This card number contains digits 5-9 which when doubled exceed 9, triggering line 45 (digitInteger -= 9)
        assertTrue(CardUtils.isValidCardNumber("5555555555554444")) // MasterCard
    }

    @Test
    fun `test isValidCardNumber with valid Luhn but invalid length`() {
        // Line 17: Tests the case where isValidLuhnNumber returns true but isValidCardLength returns false
        // Card number "424242424242" has valid Luhn checksum but is too short for Visa (needs 16 digits)
        assertFalse(CardUtils.isValidCardNumber("424242424242"))
    }

    @Test
    fun `test isValidCardLength with recognized brand but invalid length`() {
        // Line 70: Tests the 'in' operator false branch
        // "42424242" is recognized as Visa (not Unknown) but length 8 is not in Visa's valid lengths
        assertFalse(CardUtils.isValidCardLength("42424242"))
    }

    @Test
    fun `test isValidCardNumber with spaces and hyphens`() {
        assertTrue(CardUtils.isValidCardNumber("4242-4242-4242-4242"))
        assertTrue(CardUtils.isValidCardNumber("4242 4242 4242 4242"))
    }

    @Test
    fun `test isValidCardNumber with null`() {
        assertFalse(CardUtils.isValidCardNumber(null))
    }

    @Test
    fun `test isValidLuhnNumber with non-digit characters`() {
        assertFalse(CardUtils.isValidLuhnNumber("4242-4242-4242-4242"))
        assertFalse(CardUtils.isValidLuhnNumber("4242 4242 4242 4242"))
        assertFalse(CardUtils.isValidLuhnNumber("424242424242424a"))
    }

    @Test
    fun `test getPossibleCardBrand with null`() {
        assertEquals(CardBrand.Unknown, CardUtils.getPossibleCardBrand(null, false))
        assertEquals(CardBrand.Unknown, CardUtils.getPossibleCardBrand(null, true))
    }

    @Test
    fun `test getPossibleCardBrand with blank string`() {
        assertEquals(CardBrand.Unknown, CardUtils.getPossibleCardBrand("", false))
        assertEquals(CardBrand.Unknown, CardUtils.getPossibleCardBrand("  ", true))
    }

    @Test
    fun `test removeSpacesAndHyphens with null`() {
        assertEquals(null, CardUtils.removeSpacesAndHyphens(null))
    }

    @Test
    fun `test removeSpacesAndHyphens with blank string`() {
        assertEquals(null, CardUtils.removeSpacesAndHyphens(""))
        assertEquals(null, CardUtils.removeSpacesAndHyphens("   "))
    }

    @Test
    fun `test removeSpacesAndHyphens with spaces and hyphens`() {
        assertEquals("4242424242424242", CardUtils.removeSpacesAndHyphens("4242-4242-4242-4242"))
        assertEquals("4242424242424242", CardUtils.removeSpacesAndHyphens("4242 4242 4242 4242"))
        assertEquals("4242424242424242", CardUtils.removeSpacesAndHyphens("4242 - 4242 - 4242 - 4242"))
    }

    @Test
    fun `test removeSpacesAndHyphens with no spaces or hyphens`() {
        assertEquals("4242424242424242", CardUtils.removeSpacesAndHyphens("4242424242424242"))
    }

    @Test
    fun `test formatCardNumber with input longer than max length`() {
        // Test that input is truncated before formatting
        val longInput = "42424242424242424242424242424242" // 32 digits
        val result = CardUtils.formatCardNumber(longInput, CardBrand.Visa)
        // Input is truncated to maxCardLength (19), then spaces are added
        // Result should be formatted but truncated input
        assertTrue(result.startsWith("4242"))
        assertTrue(result.contains(" "))
    }

    @Test
    fun `test formatCardNumber when character at index is already a space`() {
        // Input already has proper spacing
        val input = "4242 4242 4242 4242"
        assertEquals("4242 4242 4242 4242", CardUtils.formatCardNumber(input, CardBrand.Visa))
    }

    @Test
    fun `test isValidCardLength with shouldNormalize true`() {
        assertTrue(CardUtils.isValidCardLength("4242 4242 4242 4242", shouldNormalize = true))
        assertFalse(CardUtils.isValidCardLength("4242 4242", shouldNormalize = true))
    }

    @Test
    fun `test isValidCardLength with shouldNormalize false`() {
        // Without normalization, spaces make the length invalid
        assertFalse(CardUtils.isValidCardLength("4242 4242 4242 4242", shouldNormalize = false))
        assertTrue(CardUtils.isValidCardLength("4242424242424242", shouldNormalize = false))
    }
}
