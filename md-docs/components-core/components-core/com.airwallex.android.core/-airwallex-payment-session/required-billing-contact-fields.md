//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexPaymentSession](index.md)/[requiredBillingContactFields](required-billing-contact-fields.md)

# requiredBillingContactFields

[androidJvm]\
open override val [requiredBillingContactFields](required-billing-contact-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;? = null

Billing contact fields the SDK should collect on the new-card screen. `null` preserves legacy behavior (derived from [isBillingInformationRequired](is-billing-information-required.md) / [isEmailRequired](is-email-required.md)).
