//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexSession](index.md)/[requiredBillingContactFields](required-billing-contact-fields.md)

# requiredBillingContactFields

[androidJvm]\
open val [requiredBillingContactFields](required-billing-contact-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;?

Billing contact fields the SDK should collect (and validate) on the new-card payment screen. `null` means &quot;derive from the legacy [isBillingInformationRequired](is-billing-information-required.md) / [isEmailRequired](is-email-required.md) flags&quot; so unmodified integrations keep current behavior. An empty set hides the entire billing section.

See [resolvedRequiredBillingContactFields](../resolved-required-billing-contact-fields.md) for the effective set.
