//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[fetchAvailablePaymentMethodsAndConsents](fetch-available-payment-methods-and-consents.md)

# fetchAvailablePaymentMethodsAndConsents

[androidJvm]\
suspend fun [fetchAvailablePaymentMethodsAndConsents](fetch-available-payment-methods-and-consents.md)(session: [AirwallexSession](../-airwallex-session/index.md)): [Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[Pair](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md)&gt;, [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)&gt;&gt;&gt;

Fetch available payment methods and consents (suspend function)

#### Return

[Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html) containing a [Pair](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-pair/index.html) of payment methods list and consents list

#### Parameters

androidJvm

| | |
|---|---|
| session | an [AirwallexSession](../-airwallex-session/index.md) for fetching payment methods and consents |
