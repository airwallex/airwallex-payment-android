package com.airwallex.android.core.util

import com.airwallex.android.core.RequiredBillingContactField
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.model.Address
import com.airwallex.android.core.model.Billing
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class BillingValidationTest {

    private fun billing(
        firstName: String? = null,
        email: String? = null,
        phone: String? = null,
        address: Address? = null,
    ) = Billing.Builder()
        .setFirstName(firstName)
        .setEmail(email)
        .setPhone(phone)
        .setAddress(address)
        .build()

    private fun fullAddress(
        countryCode: String = "US",
        street: String = "1 Main St",
        city: String = "City",
        state: String = "CA",
        postcode: String = "94000",
    ) = Address.Builder()
        .setCountryCode(countryCode)
        .setStreet(street)
        .setCity(city)
        .setState(state)
        .setPostcode(postcode)
        .build()

    @Test fun `empty required-set bypasses all checks`() {
        assertNull(null.validateForRequiredFields(emptySet()))
        assertNull(billing().validateForRequiredFields(emptySet()))
    }

    @Test fun `NAME requires non-blank firstName`() {
        val req = setOf(RequiredBillingContactField.NAME)
        assertMessage("Billing name is required", null.validateForRequiredFields(req))
        assertMessage("Billing name is required", billing(firstName = "  ").validateForRequiredFields(req))
        assertNull(billing(firstName = "Alice").validateForRequiredFields(req))
    }

    @Test fun `EMAIL requires valid format`() {
        val req = setOf(RequiredBillingContactField.EMAIL)
        assertMessage("Billing email is required", billing(email = null).validateForRequiredFields(req))
        assertMessage("Billing email is required", billing(email = "notanemail").validateForRequiredFields(req))
        assertNull(billing(email = "user@example.com").validateForRequiredFields(req))
    }

    @Test fun `PHONE requires E164 shape with optional leading plus`() {
        val req = setOf(RequiredBillingContactField.PHONE)
        assertNotNull(billing(phone = null).validateForRequiredFields(req))
        // leading 0 (with or without +) is rejected — country digit must be 1-9
        assertNotNull(billing(phone = "+0123456").validateForRequiredFields(req))
        assertNotNull(billing(phone = "0123456").validateForRequiredFields(req))
        // spaces and formatting are rejected
        assertNotNull(billing(phone = "+1 234 567").validateForRequiredFields(req))
        // 2 digits ok (min), with or without +
        assertNull(billing(phone = "+12").validateForRequiredFields(req))
        assertNull(billing(phone = "12").validateForRequiredFields(req))
        assertNull(billing(phone = "+15551234567").validateForRequiredFields(req))
        // leading + is optional — merchants may strip it before passing in
        assertNull(billing(phone = "15551234567").validateForRequiredFields(req))
        // 16 digits = invalid (E.164 max 15)
        assertNotNull(billing(phone = "+1234567890123456").validateForRequiredFields(req))
        assertNotNull(billing(phone = "1234567890123456").validateForRequiredFields(req))
    }

    @Test fun `ADDRESS requires every field plus valid country code`() {
        val req = setOf(RequiredBillingContactField.ADDRESS)
        assertNotNull(billing(address = null).validateForRequiredFields(req))
        // Non-ISO codes fail (Address.Builder uppercases input, so "ZZ" gets through formatting
        // and the validator is the layer that rejects it).
        assertNotNull(billing(address = fullAddress(countryCode = "ZZ")).validateForRequiredFields(req))
        assertNotNull(billing(address = fullAddress(street = "")).validateForRequiredFields(req))
        assertNotNull(billing(address = fullAddress(city = " ")).validateForRequiredFields(req))
        assertNotNull(billing(address = fullAddress(state = "")).validateForRequiredFields(req))
        assertNotNull(billing(address = fullAddress(postcode = "")).validateForRequiredFields(req))
        assertNull(billing(address = fullAddress()).validateForRequiredFields(req))
        // Address.Builder uppercases; lowercase input becomes valid.
        assertNull(billing(address = fullAddress(countryCode = "us")).validateForRequiredFields(req))
    }

    @Test fun `COUNTRY_CODE alone validates the picker only`() {
        val req = setOf(RequiredBillingContactField.COUNTRY_CODE)
        val onlyCountry = Address.Builder().setCountryCode("US").build()
        assertNull(billing(address = onlyCountry).validateForRequiredFields(req))
        assertNotNull(billing(address = Address.Builder().setCountryCode("ZZ").build()).validateForRequiredFields(req))
        assertNotNull(billing(address = null).validateForRequiredFields(req))
    }

    @Test fun `ADDRESS supersedes COUNTRY_CODE - missing rows still fail`() {
        val req = setOf(
            RequiredBillingContactField.ADDRESS,
            RequiredBillingContactField.COUNTRY_CODE,
        )
        // Only country: ADDRESS still requires the rest
        val onlyCountry = Address.Builder().setCountryCode("US").build()
        assertNotNull(billing(address = onlyCountry).validateForRequiredFields(req))
        assertNull(billing(address = fullAddress()).validateForRequiredFields(req))
    }

    @Test fun `multiple fields all checked - first failure wins`() {
        val req = setOf(
            RequiredBillingContactField.NAME,
            RequiredBillingContactField.EMAIL,
            RequiredBillingContactField.PHONE,
        )
        assertMessage("Billing name is required", billing().validateForRequiredFields(req))
        assertMessage(
            "Billing email is required",
            billing(firstName = "X").validateForRequiredFields(req)
        )
        assertMessage(
            "Billing phone is required",
            billing(firstName = "X", email = "x@y.com").validateForRequiredFields(req)
        )
        assertNull(
            billing(firstName = "X", email = "x@y.com", phone = "+15551234567")
                .validateForRequiredFields(req)
        )
    }

    private fun assertMessage(expected: String, ex: InvalidParamsException?) {
        assertNotNull("Expected exception with message containing '$expected'", ex)
        val nonNull = checkNotNull(ex)
        assertTrue(
            "Expected '${nonNull.message}' to contain '$expected'",
            nonNull.message?.contains(expected) == true
        )
    }
}
