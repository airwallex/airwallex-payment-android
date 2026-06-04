//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[RequiredBillingContactField](index.md)

# RequiredBillingContactField

[androidJvm]\
enum [RequiredBillingContactField](index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[RequiredBillingContactField](index.md)&gt; 

Billing contact fields that the SDK can collect on the new-card payment screen and validate in the headless checkout path. Mirrors iOS's `AWXRequiredBillingContactFields` option set.

**Scope: card payments only.** This setting drives the card-entry UI and the headless-checkout billing validation for cards. It does NOT propagate to Google Pay or other LPM (Local Payment Method) flows — Google Pay's billing contact is configured separately through [com.airwallex.android.core.GooglePayOptions](../-google-pay-options/index.md) (`billingAddressRequired`, `billingAddressParameters`, `emailRequired`).

Configure via [AirwallexSession.requiredBillingContactFields](../-airwallex-session/required-billing-contact-fields.md). An empty set hides the entire billing section; `null` (the default) derives the set from the legacy [AirwallexSession.isBillingInformationRequired](../-airwallex-session/is-billing-information-required.md) / [AirwallexSession.isEmailRequired](../-airwallex-session/is-email-required.md) flags so existing integrations keep their current behavior.

`ADDRESS` and `COUNTRY_CODE` are mutually exclusive in the UI: when both are set, `ADDRESS` wins and the standalone country picker is suppressed.

## Entries

| | |
|---|---|
| [NAME](-n-a-m-e/index.md) | [androidJvm]<br>[NAME](-n-a-m-e/index.md) |
| [EMAIL](-e-m-a-i-l/index.md) | [androidJvm]<br>[EMAIL](-e-m-a-i-l/index.md) |
| [PHONE](-p-h-o-n-e/index.md) | [androidJvm]<br>[PHONE](-p-h-o-n-e/index.md) |
| [ADDRESS](-a-d-d-r-e-s-s/index.md) | [androidJvm]<br>[ADDRESS](-a-d-d-r-e-s-s/index.md) |
| [COUNTRY_CODE](-c-o-u-n-t-r-y_-c-o-d-e/index.md) | [androidJvm]<br>[COUNTRY_CODE](-c-o-u-n-t-r-y_-c-o-d-e/index.md) |

## Properties

| Name | Summary |
|---|---|
| [entries](entries.md) | [androidJvm]<br>val [entries](entries.md): [EnumEntries](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.enums/-enum-entries/index.html)&lt;[RequiredBillingContactField](index.md)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [name](../../com.airwallex.android.core.model/-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-372974862%2FProperties%2F1424399983) | [androidJvm]<br>val [name](../../com.airwallex.android.core.model/-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-372974862%2FProperties%2F1424399983): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [ordinal](../../com.airwallex.android.core.model/-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-739389684%2FProperties%2F1424399983) | [androidJvm]<br>val [ordinal](../../com.airwallex.android.core.model/-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-739389684%2FProperties%2F1424399983): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [valueOf](value-of.md) | [androidJvm]<br>fun [valueOf](value-of.md)(value: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [RequiredBillingContactField](index.md)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [androidJvm]<br>fun [values](values.md)(): [Array](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-array/index.html)&lt;[RequiredBillingContactField](index.md)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |
