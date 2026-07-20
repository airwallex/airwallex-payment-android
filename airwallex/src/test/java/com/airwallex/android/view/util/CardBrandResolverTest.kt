package com.airwallex.android.view.util

import com.airwallex.android.core.model.PaymentMethod
import kotlin.test.assertEquals
import kotlin.test.assertNull
import org.junit.Test

class CardBrandResolverTest {

    private fun card(brand: String?, bin: String?) = PaymentMethod.Card.Builder()
        .setBrand(brand)
        .setBin(bin)
        .build()

    @Test
    fun `mastercard with maestro bin resolves to maestro`() {
        assertEquals("maestro", card(brand = "mastercard", bin = "670000").resolvedBrandName())
        assertEquals("maestro", card(brand = "mastercard", bin = "500000").resolvedBrandName())
    }

    @Test
    fun `mastercard with mastercard bin stays mastercard`() {
        assertEquals("mastercard", card(brand = "mastercard", bin = "520000").resolvedBrandName())
    }

    @Test
    fun `mastercard match is case-insensitive`() {
        assertEquals("maestro", card(brand = "MasterCard", bin = "670000").resolvedBrandName())
    }

    @Test
    fun `mastercard with missing bin stays mastercard`() {
        assertEquals("mastercard", card(brand = "mastercard", bin = null).resolvedBrandName())
    }

    @Test
    fun `other brands are never changed`() {
        assertEquals("visa", card(brand = "visa", bin = "670000").resolvedBrandName())
        assertEquals("amex", card(brand = "amex", bin = "500000").resolvedBrandName())
    }

    @Test
    fun `null brand returns null`() {
        assertNull(card(brand = null, bin = "670000").resolvedBrandName())
    }
}
