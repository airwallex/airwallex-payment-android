//[airwallex](../../../index.md)/[com.airwallex.android.view](../index.md)/[AddPaymentMethodViewModel](index.md)/[createBilling](create-billing.md)

# createBilling

[androidJvm]\
fun [createBilling](create-billing.md)(name: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), email: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), phoneNumber: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), state: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), city: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), street: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), postcode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Billing](../../../../components-core/components-core/com.airwallex.android.core.model/-billing/index.md)?

Build a [Billing](../../../../components-core/components-core/com.airwallex.android.core.model/-billing/index.md) containing only the fields the merchant asked for via [resolvedBillingFields](resolved-billing-fields.md). Returns `null` when the set is empty so no billing payload is sent.
