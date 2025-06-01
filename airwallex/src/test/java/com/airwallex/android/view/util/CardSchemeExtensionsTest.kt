package com.airwallex.android.view.util

import com.airwallex.android.R
import com.airwallex.android.core.CardBrand
import com.airwallex.android.core.model.CardScheme
import org.junit.Test
import kotlin.test.assertEquals

class CardSchemeExtensionsTest {

    @Test
    fun `toSupportedIcons should return empty list when input is empty`() {
        // Given
        val emptyList = emptyList<CardScheme>()

        // When
        val result = emptyList.toSupportedIcons()

        // Then
        assert(result.isEmpty())
    }

    @Test
    fun `toSupportedIcons should return correct icon for visa card scheme`() {
        // Given
        val visaScheme = CardScheme("visa")
        val expectedIcon = CardBrand.Visa.icon

        // When
        val result = listOf(visaScheme).toSupportedIcons()

        // Then
        assertEquals(1, result.size)
        assertEquals(expectedIcon, result[0])
    }

    @Test
    fun `toSupportedIcons should return correct icon for mastercard scheme`() {
        // Given
        val mastercardScheme = CardScheme("mastercard")
        val expectedIcon = CardBrand.MasterCard.icon

        // When
        val result = listOf(mastercardScheme).toSupportedIcons()
        // Then
        assertEquals(1, result.size)
        assertEquals(expectedIcon, result[0])
    }

    @Test
    fun `toSupportedIcons should return correct icon for amex scheme`() {
        // Given
        val amexScheme = CardScheme("amex")
        val expectedIcon = CardBrand.Amex.icon

        // When
        val result = listOf(amexScheme).toSupportedIcons()
        // Then
        assertEquals(1, result.size)
        assertEquals(expectedIcon, result[0])
    }

    @Test
    fun `toSupportedIcons should filter out unsupported card schemes`() {
        // Given
        val schemes = listOf(
            CardScheme("visa"),
            CardScheme("unsupported"),
            CardScheme("mastercard")
        )
        val expectedIcons = listOf(
            CardBrand.Visa.icon,
            CardBrand.MasterCard.icon
        )

        // When
        val result = schemes.toSupportedIcons()
        // Then
        assertEquals(2, result.size)
        assertEquals(expectedIcons, result)
    }

    @Test
    fun `toSupportedIcons should handle multiple schemes including duplicates`() {
        // Given
        val schemes = listOf(
            CardScheme("visa"),
            CardScheme("visa"),
            CardScheme("mastercard")
        )
        val expectedIcons = listOf(
            CardBrand.Visa.icon,
            CardBrand.Visa.icon,
            CardBrand.MasterCard.icon
        )

        // When
        val result = schemes.toSupportedIcons()
        // Then
        assertEquals(3, result.size)
        assertEquals(expectedIcons, result)
    }
}
