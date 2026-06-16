//[airwallex](../../../index.md)/[com.airwallex.android.view](../index.md)/[AddPaymentMethodViewModel](index.md)/[getPostcodeValidationMessage](get-postcode-validation-message.md)

# getPostcodeValidationMessage

[androidJvm]\

@[StringRes](https://developer.android.com/reference/kotlin/androidx/annotation/StringRes.html)

fun [getPostcodeValidationMessage](get-postcode-validation-message.md)(input: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)?

Postcode validation: presence + country-specific regex.

Any field that is visible is required, so callers are expected to gate on [AddressSpec.hasPostcode](../../../../components-core/components-core/com.airwallex.android.core.util/-address-spec/has-postcode.md) before invoking — by the time we get here, the field is being collected and must be non-blank.

Returns:

- 
   R.string.airwallex_required when blank
- 
   R.string.airwallex_please_enter_valid_value when non-blank but the country's pattern doesn't match
- 
   null when valid (or non-blank for a country with no declared pattern)
