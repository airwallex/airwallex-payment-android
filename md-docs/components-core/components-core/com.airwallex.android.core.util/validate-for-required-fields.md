//[components-core](../../index.md)/[com.airwallex.android.core.util](index.md)/[validateForRequiredFields](validate-for-required-fields.md)

# validateForRequiredFields

[androidJvm]\
fun [Billing](../com.airwallex.android.core.model/-billing/index.md)?.[validateForRequiredFields](validate-for-required-fields.md)(requiredFields: [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../com.airwallex.android.core/-required-billing-contact-field/index.md)&gt;): [InvalidParamsException](../com.airwallex.android.core.exception/-invalid-params-exception/index.md)?

Verify a [Billing](../com.airwallex.android.core.model/-billing/index.md) payload satisfies every field in [requiredFields](validate-for-required-fields.md). Returns `null` when valid, or an [InvalidParamsException](../com.airwallex.android.core.exception/-invalid-params-exception/index.md) describing the first missing/ invalid field. Mirrors the iOS validation rules in `AWXDefaultProvider+Extensions.swift`.

This is the card-flow validator only. Google Pay billing is configured via [com.airwallex.android.core.GooglePayOptions](../com.airwallex.android.core/-google-pay-options/index.md) and validated separately by Google Pay itself — this function is not invoked on the Google Pay path.

- 
   `NAME`: `firstName` must be non-blank.
- 
   `EMAIL`: must match the SDK's email regex.
- 
   `PHONE`: must match the E.164 shape (`+?[1-9]\d{1,14}`); the leading `+` is optional
- 
   `ADDRESS`: street, city, state, postcode all non-blank, plus a 2-letter uppercase ISO country code.
- 
   `COUNTRY_CODE`: 2-letter uppercase ISO country code (suppressed when ADDRESS is also required — ADDRESS already covers it).
