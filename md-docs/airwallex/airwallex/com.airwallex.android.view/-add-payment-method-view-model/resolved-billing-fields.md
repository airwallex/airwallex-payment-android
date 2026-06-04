//[airwallex](../../../index.md)/[com.airwallex.android.view](../index.md)/[AddPaymentMethodViewModel](index.md)/[resolvedBillingFields](resolved-billing-fields.md)

# resolvedBillingFields

[androidJvm]\
val [resolvedBillingFields](resolved-billing-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../../../../components-core/components-core/com.airwallex.android.core/-required-billing-contact-field/index.md)&gt;

Resolved (non-null) set of billing-contact fields to render on this screen. Distinct from [AirwallexSession.requiredBillingContactFields](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/required-billing-contact-fields.md) which is the raw nullable merchant configuration; here we've already collapsed the &quot;null → derive from legacy booleans&quot; rule via [resolvedRequiredBillingContactFields](../../../../components-core/components-core/com.airwallex.android.core/resolved-required-billing-contact-fields.md).
