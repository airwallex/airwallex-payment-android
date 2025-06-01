package com.airwallex.android.view.util

import com.airwallex.android.core.CardBrand
import org.junit.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CvvUtilsTest {

    @Test
    fun `isValidCvc returns true for valid 3-digit CVV for non-Amex cards`() {
        // Test for Visa
        assertTrue("123".isValidCvc(CardBrand.Visa))
        
        // Test for Mastercard
        assertTrue("456".isValidCvc(CardBrand.MasterCard))
        
        // Test for Discover
        assertTrue("789".isValidCvc(CardBrand.DISCOVER))
        
        // Test for JCB
        assertTrue("012".isValidCvc(CardBrand.JCB))
    }
    
    @Test
    fun `isValidCvc returns false for invalid length CVV for non-Amex cards`() {
        // Too short
        assertFalse("12".isValidCvc(CardBrand.Visa))
        
        // Too long
        assertFalse("1234".isValidCvc(CardBrand.MasterCard))
        
        // Empty string
        assertFalse("".isValidCvc(CardBrand.DISCOVER))
    }
    
    @Test
    fun `isValidCvc returns true for valid 4-digit CVV for Amex`() {
        assertTrue("1234".isValidCvc(CardBrand.Amex))
    }
    
    @Test
    fun `isValidCvc returns false for invalid length CVV for Amex`() {
        // Too short
        assertFalse("123".isValidCvc(CardBrand.Amex))
        
        // Too long
        assertFalse("12345".isValidCvc(CardBrand.Amex))
        
        // Empty string
        assertFalse("".isValidCvc(CardBrand.Amex))
    }
    
    @Test
    fun `isValidCvc returns false for non-numeric input`() {
        // Non-numeric characters
        assertFalse("12a".isValidCvc(CardBrand.Visa))
        assertFalse("1a3".isValidCvc(CardBrand.Visa))
        assertFalse("a23".isValidCvc(CardBrand.Visa))
        assertFalse("abc".isValidCvc(CardBrand.Visa))
        
        // Special characters
        assertFalse("12#".isValidCvc(CardBrand.Visa))
        assertFalse("1-3".isValidCvc(CardBrand.Visa))
        assertFalse("1 3".isValidCvc(CardBrand.Visa))
        
        // For Amex
        assertFalse("123a".isValidCvc(CardBrand.Amex))
        assertFalse("1a34".isValidCvc(CardBrand.Amex))
        assertFalse("a234".isValidCvc(CardBrand.Amex))
        assertFalse("abcd".isValidCvc(CardBrand.Amex))
    }
    
    @Test
    fun `isValidCvc handles edge cases`() {
        // Minimum valid values
        assertTrue("000".isValidCvc(CardBrand.Visa))
        assertTrue("0000".isValidCvc(CardBrand.Amex))
        
        // Maximum valid values
        assertTrue("999".isValidCvc(CardBrand.Visa))
        assertTrue("9999".isValidCvc(CardBrand.Amex))
        
        // Very long strings (should be caught by length check before number parsing)
        assertFalse("1234567890".isValidCvc(CardBrand.Visa))
        assertFalse("1234567890".isValidCvc(CardBrand.Amex))
    }
}
