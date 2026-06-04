//[components-core](../../index.md)/[com.airwallex.android.core](index.md)/[resolvedRequiredBillingContactFields](resolved-required-billing-contact-fields.md)

# resolvedRequiredBillingContactFields

[androidJvm]\
val [AirwallexSession](-airwallex-session/index.md).[resolvedRequiredBillingContactFields](resolved-required-billing-contact-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](-required-billing-contact-field/index.md)&gt;

Effective set of billing contact fields, resolving `null` to the legacy boolean-derived defaults that match Android's pre-existing UI:

- 
   `NAME` is always included (the cardholder-name row was always shown).
- 
   [AirwallexSession.isBillingInformationRequired](-airwallex-session/is-billing-information-required.md) = true → adds `ADDRESS` + `PHONE`.
- 
   [AirwallexSession.isEmailRequired](-airwallex-session/is-email-required.md) = true → adds `EMAIL`.

Merchants who explicitly call `setRequiredBillingContactFields(...)` bypass this derivation entirely; an empty set hides the entire billing section including the cardholder-name row.
