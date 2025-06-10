package com.airwallex.android.view.util

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class EmailExtensionsTest {

    @Test
    fun `isValidEmail should return true for valid email formats`() {
        // Standard email formats
        assertTrue("test@example.com".isValidEmail())
        assertTrue("user.name@example.com".isValidEmail())
        assertTrue("user-name@example.com".isValidEmail())

        // Emails with numbers and special characters
        assertTrue("user123@example.com".isValidEmail())
        assertTrue("123user@example.com".isValidEmail())
    }

    @Test
    fun `isValidEmail should return false for empty or whitespace`() {
        assertFalse("".isValidEmail())
        assertFalse(" ".isValidEmail())
        assertFalse("   ".isValidEmail())
    }

    @Test
    fun `isValidEmail should return false for emails with missing parts`() {
        assertFalse("@example.com".isValidEmail()) // Missing local part
        assertFalse("user@".isValidEmail()) // Missing domain
    }

    @Test
    fun `isValidEmail should return false for emails with spaces`() {
        assertFalse("user name@example.com".isValidEmail()) // Space in local part
        assertFalse("user@exa mple.com".isValidEmail()) // Space in domain
    }

    @Test
    fun `isValidEmail should allow various email formats`() {
        // The current implementation allows:
        // 1. Consecutive dots in domain
        val email1 = "test@example..com"
        assertTrue(email1, email1.isValidEmail())

        // 2. Standard TLDs (2-6 characters)
        val validTlds = listOf("com", "org", "net", "io", "co.uk", "com.au")
        validTlds.forEach { tld ->
            val email = "test@example.$tld"
            assertTrue("Should accept TLD: $tld", email.isValidEmail())
        }

        // 3. Domains starting/ending with hyphens
        val email2 = "test@-example.com"
        val email3 = "test@example-.com"
        assertTrue(email2, email2.isValidEmail())
        assertTrue(email3, email3.isValidEmail())

        // 4. Various special characters in local part
        val specialChars = listOf(
            "user.name@example.com",
            "user-name@example.com",
            "user+tag@example.com",
            "user%name@example.com"
        )

        specialChars.forEach { email ->
            assertTrue(email, email.isValidEmail())
        }
    }

    @Test
    fun `isValidEmail should reject invalid TLDs`() {
        // TLD too short (1 character)
        assertFalse("test@example.c".isValidEmail())
    }

    @Test
    fun `isValidEmail should handle edge cases`() {
        // Test with various TLD lengths
        assertTrue("test@example.abcd".isValidEmail())

        // Test with subdomains
        assertTrue("test@sub.example.com".isValidEmail())

        // Test with numbers in domain
        assertTrue("test@example123.com".isValidEmail())

        // Test with hyphens in domain (but not at start/end)
        assertTrue("test@valid-domain.com".isValidEmail())
    }
}
