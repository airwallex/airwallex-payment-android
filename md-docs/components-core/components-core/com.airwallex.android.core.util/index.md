//[components-core](../../index.md)/[com.airwallex.android.core.util](index.md)

# Package-level declarations

## Types

| Name | Summary |
|---|---|
| [AirwallexJsonUtils](-airwallex-json-utils/index.md) | [androidJvm]<br>object [AirwallexJsonUtils](-airwallex-json-utils/index.md) |
| [BuildConfigHelper](-build-config-helper/index.md) | [androidJvm]<br>object [BuildConfigHelper](-build-config-helper/index.md) |
| [BuildHelper](-build-helper/index.md) | [androidJvm]<br>object [BuildHelper](-build-helper/index.md) |
| [CardUtils](-card-utils/index.md) | [androidJvm]<br>object [CardUtils](-card-utils/index.md) |
| [CurrencyUtils](-currency-utils/index.md) | [androidJvm]<br>object [CurrencyUtils](-currency-utils/index.md) |
| [SessionUtils](-session-utils/index.md) | [androidJvm]<br>object [SessionUtils](-session-utils/index.md) |

## Functions

| Name | Summary |
|---|---|
| [isValidE164Phone](is-valid-e164-phone.md) | [androidJvm]<br>fun [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html).[isValidE164Phone](is-valid-e164-phone.md)(): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>E.164: leading `+` or start immediately, country digit 1-9, then 1-14 more digits (15 max total). Whitespace and formatting characters are NOT allowed — callers should strip them first if needed. |
| [validateForRequiredFields](validate-for-required-fields.md) | [androidJvm]<br>fun [Billing](../com.airwallex.android.core.model/-billing/index.md)?.[validateForRequiredFields](validate-for-required-fields.md)(requiredFields: [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../com.airwallex.android.core/-required-billing-contact-field/index.md)&gt;): [InvalidParamsException](../com.airwallex.android.core.exception/-invalid-params-exception/index.md)?<br>Verify a [Billing](../com.airwallex.android.core.model/-billing/index.md) payload satisfies every field in [requiredFields](validate-for-required-fields.md). Returns `null` when valid, or an [InvalidParamsException](../com.airwallex.android.core.exception/-invalid-params-exception/index.md) describing the first missing/ invalid field. Mirrors the iOS validation rules in `AWXDefaultProvider+Extensions.swift`. |
