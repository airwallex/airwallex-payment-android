package com.airwallex.android.core.model

import kotlin.test.assertEquals
import org.junit.Test

class CardSchemeExtensionsTest {

    @Test
    fun `appends maestro when mastercard is present`() {
        val schemes = listOf(CardScheme("visa"), CardScheme("mastercard"))
        assertEquals(
            listOf("visa", "mastercard", "maestro"),
            schemes.withMaestroIfMasterCard().map { it.name }
        )
    }

    @Test
    fun `matches mastercard case-insensitively`() {
        val schemes = listOf(CardScheme("MasterCard"))
        assertEquals(
            listOf("MasterCard", "maestro"),
            schemes.withMaestroIfMasterCard().map { it.name }
        )
    }

    @Test
    fun `does nothing when mastercard is absent`() {
        val schemes = listOf(CardScheme("visa"), CardScheme("amex"))
        assertEquals(schemes, schemes.withMaestroIfMasterCard())
    }

    @Test
    fun `does not duplicate maestro when already present`() {
        val schemes = listOf(CardScheme("mastercard"), CardScheme("maestro"))
        assertEquals(schemes, schemes.withMaestroIfMasterCard())
    }

    @Test
    fun `does nothing for empty list`() {
        assertEquals(emptyList(), emptyList<CardScheme>().withMaestroIfMasterCard())
    }
}
