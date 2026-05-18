package com.airwallex.android.core

import org.junit.Assert.assertEquals
import org.junit.Test
import java.math.BigDecimal

class AirwallexSessionTest {

    private fun fakeSession(
        explicit: Set<RequiredBillingContactField>?,
        billingRequired: Boolean,
        emailRequired: Boolean,
    ): AirwallexSession = object : AirwallexSession() {
        override val customerId: String? = null
        override val shipping = null
        @Suppress("DEPRECATION_OVERRIDE")
        override val isBillingInformationRequired: Boolean = billingRequired
        @Suppress("DEPRECATION_OVERRIDE")
        override val isEmailRequired: Boolean = emailRequired
        override val currency: String = "USD"
        override val countryCode: String = "US"
        override val amount: BigDecimal = BigDecimal.ONE
        override val returnUrl: String? = null
        override val googlePayOptions = null
        override val paymentMethods: List<String>? = null
        override val clientSecret: String? = null
        override val requiredBillingContactFields = explicit
    }

    @Test
    fun `null explicit value derives NAME plus address fields when billing required`() {
        val resolved = fakeSession(
            explicit = null,
            billingRequired = true,
            emailRequired = false
        ).resolvedRequiredBillingContactFields
        assertEquals(
            setOf(
                RequiredBillingContactField.NAME,
                RequiredBillingContactField.ADDRESS,
                RequiredBillingContactField.PHONE,
            ),
            resolved
        )
    }

    @Test
    fun `null explicit value adds EMAIL when isEmailRequired`() {
        val resolved = fakeSession(
            explicit = null,
            billingRequired = true,
            emailRequired = true
        ).resolvedRequiredBillingContactFields
        assertEquals(
            setOf(
                RequiredBillingContactField.NAME,
                RequiredBillingContactField.ADDRESS,
                RequiredBillingContactField.PHONE,
                RequiredBillingContactField.EMAIL,
            ),
            resolved
        )
    }

    @Test
    fun `null explicit value with both legacy booleans false yields NAME only`() {
        val resolved = fakeSession(
            explicit = null,
            billingRequired = false,
            emailRequired = false
        ).resolvedRequiredBillingContactFields
        assertEquals(setOf(RequiredBillingContactField.NAME), resolved)
    }

    @Test
    fun `explicit empty set wins and hides everything`() {
        val resolved = fakeSession(
            explicit = emptySet(),
            billingRequired = true,
            emailRequired = true
        ).resolvedRequiredBillingContactFields
        assertEquals(emptySet<RequiredBillingContactField>(), resolved)
    }

    @Test
    fun `explicit set wins over legacy booleans`() {
        val explicit = setOf(
            RequiredBillingContactField.EMAIL,
            RequiredBillingContactField.COUNTRY_CODE,
        )
        val resolved = fakeSession(
            explicit = explicit,
            billingRequired = true,
            emailRequired = false
        ).resolvedRequiredBillingContactFields
        assertEquals(explicit, resolved)
    }
}
