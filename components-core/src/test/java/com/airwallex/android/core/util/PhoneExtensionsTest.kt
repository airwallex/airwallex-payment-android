package com.airwallex.android.core.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PhoneExtensionsTest {

    @Test fun `accepts valid E164 numbers`() {
        // 2-digit minimum
        assertTrue("+12".isValidE164Phone())
        assertTrue("+15551234567".isValidE164Phone())
        assertTrue("+447700900000".isValidE164Phone())
        // 15 digits max
        assertTrue("+123456789012345".isValidE164Phone())
    }

    @Test fun `accepts E164 with leading plus optional`() {
        // Same numbers without the leading + must also pass — the SDK is tolerant
        // of merchants who strip the + before passing the value in.
        assertTrue("15551234567".isValidE164Phone())
        assertTrue("447700900000".isValidE164Phone())
    }

    @Test fun `rejects leading zero after plus`() {
        assertFalse("+05551234567".isValidE164Phone())
    }

    @Test fun `rejects whitespace and formatting characters`() {
        assertFalse("+1 555 1234567".isValidE164Phone())
        assertFalse("+1-555-1234567".isValidE164Phone())
        assertFalse("+1.555.1234567".isValidE164Phone())
        assertFalse("(555) 123-4567".isValidE164Phone())
    }

    @Test fun `rejects too-short and too-long numbers`() {
        assertFalse("+".isValidE164Phone())
        // 1 digit
        assertFalse("+1".isValidE164Phone())
        // 16 digits
        assertFalse("+1234567890123456".isValidE164Phone())
    }

    @Test fun `rejects empty and blank`() {
        assertFalse("".isValidE164Phone())
        assertFalse("   ".isValidE164Phone())
    }
}
