package com.airwallex.android.core.util

import com.airwallex.android.core.RequiredBillingContactField
import com.airwallex.android.core.exception.InvalidParamsException
import com.airwallex.android.core.model.Billing
import java.util.Locale

private val EMAIL_REGEX = Regex("[A-Z0-9a-z._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,63}")
private val ISO_COUNTRY_CODES: Set<String> = Locale.getISOCountries().toSet()

private fun isValidCountryCode(code: String?): Boolean = code != null && code in ISO_COUNTRY_CODES

/**
 * Verify a [Billing] payload satisfies every field in [requiredFields]. Returns
 * `null` when valid, or an [InvalidParamsException] describing the first missing/
 * invalid field. Mirrors the iOS validation rules in
 * `AWXDefaultProvider+Extensions.swift`.
 *
 * This is the card-flow validator only. Google Pay billing is configured via
 * [com.airwallex.android.core.GooglePayOptions] and validated separately by Google
 * Pay itself — this function is not invoked on the Google Pay path.
 *
 * - `NAME`: `firstName` must be non-blank.
 * - `EMAIL`: must match the SDK's email regex.
 * - `PHONE`: must match the E.164 shape (`+?[1-9]\d{1,14}`); the leading `+` is optional
 * - `ADDRESS`: street non-blank, plus a 2-letter uppercase ISO country code. The
 *   state, city, and postcode checks are conditional — they only apply when the
 *   country's [AddressSpec] declares that field, since the UI hides any field
 *   the country doesn't collect (e.g. AE has no city/postcode, JP has no city).
 * - `COUNTRY_CODE`: 2-letter uppercase ISO country code (suppressed when ADDRESS
 *   is also required — ADDRESS already covers it).
 */
@Suppress("ComplexMethod")
fun Billing?.validateForRequiredFields(
    requiredFields: Set<RequiredBillingContactField>
): InvalidParamsException? {
    if (requiredFields.isEmpty()) return null

    val billing = this
    val address = billing?.address

    if (RequiredBillingContactField.NAME in requiredFields &&
        billing?.firstName.isNullOrBlank()
    ) {
        return InvalidParamsException(message = "Billing name is required")
    }

    if (RequiredBillingContactField.EMAIL in requiredFields) {
        val email = billing?.email
        if (email.isNullOrBlank() || !EMAIL_REGEX.matches(email)) {
            return InvalidParamsException(message = "Billing email is required")
        }
    }

    if (RequiredBillingContactField.PHONE in requiredFields) {
        val phone = billing?.phone
        if (phone.isNullOrBlank() || !phone.isValidE164Phone()) {
            return InvalidParamsException(message = "Billing phone is required")
        }
    }

    if (RequiredBillingContactField.ADDRESS in requiredFields) {
        val countryCode = address?.countryCode
        if (countryCode == null || countryCode !in ISO_COUNTRY_CODES) {
            return InvalidParamsException(message = "Billing country code is required")
        }
        when {
            address.street.isNullOrBlank() ->
                return InvalidParamsException(message = "Billing street is required")

            AddressSpec.hasCity(countryCode) && address.city.isNullOrBlank() ->
                return InvalidParamsException(message = "Billing city is required")

            AddressSpec.hasState(countryCode) && address.state.isNullOrBlank() ->
                return InvalidParamsException(message = "Billing state is required")

            AddressSpec.hasPostcode(countryCode) && address.postcode.isNullOrBlank() ->
                return InvalidParamsException(message = "Billing postcode is required")
        }
    } else if (RequiredBillingContactField.COUNTRY_CODE in requiredFields &&
        !isValidCountryCode(address?.countryCode)
    ) {
        return InvalidParamsException(message = "Billing country code is required")
    }

    return null
}
