package com.airwallex.android.view.util

import com.airwallex.android.R
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNull

class CountryFlagExtensionsTest {

    @Test
    fun `safeValueOf should return correct enum for valid country code`() {
        // Given
        val validCode = "US"

        // When
        val result = safeValueOf<CountryCodeType>(validCode)

        // Then
        assertEquals(CountryCodeType.US, result)
    }

    @Test
    fun `safeValueOf should return null for invalid country code`() {
        // Given
        val invalidCode = "XX"

        // When
        val result = safeValueOf<CountryCodeType>(invalidCode)

        // Then
        assertNull(result)
    }

    @Test
    fun `safeValueOf should be case insensitive`() {
        // Given
        val lowerCaseCode = "us"
        val mixedCaseCode = "Us"

        // When
        val result1 = safeValueOf<CountryCodeType>(lowerCaseCode)
        val result2 = safeValueOf<CountryCodeType>(mixedCaseCode)

        // Then
        assertEquals(CountryCodeType.US, result1)
        assertEquals(CountryCodeType.US, result2)
    }

    @Test
    fun `CountryCodeType should have correct flag resources`() {
        // Test a sample of countries to ensure flag resources are correctly mapped
        assertEquals(R.drawable.ic_flag_us, CountryCodeType.US.flagRes)
        assertEquals(R.drawable.ic_flag_gb, CountryCodeType.GB.flagRes)
        assertEquals(R.drawable.ic_flag_jp, CountryCodeType.JP.flagRes)
        assertEquals(R.drawable.ic_flag_au, CountryCodeType.AU.flagRes)
        assertEquals(R.drawable.ic_flag_cn, CountryCodeType.CN.flagRes)
    }

    @Test
    fun `UNKNOWN country code should have world flag`() {
        // Given
        val unknownCode = "UNKNOWN"

        // When
        val result = safeValueOf<CountryCodeType>(unknownCode)

        // Then
        assertEquals(CountryCodeType.UNKNOWN, result)
        assertEquals(R.drawable.ic_flag_world, CountryCodeType.UNKNOWN.flagRes)
    }
}
