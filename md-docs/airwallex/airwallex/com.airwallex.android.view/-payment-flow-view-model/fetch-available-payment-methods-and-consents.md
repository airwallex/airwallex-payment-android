//[airwallex](../../../index.md)/[com.airwallex.android.view](../index.md)/[PaymentFlowViewModel](index.md)/[fetchAvailablePaymentMethodsAndConsents](fetch-available-payment-methods-and-consents.md)

# fetchAvailablePaymentMethodsAndConsents

[androidJvm]\
suspend fun [fetchAvailablePaymentMethodsAndConsents](fetch-available-payment-methods-and-consents.md)(): [Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[Pair](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AvailablePaymentMethodType](../../../../components-core/components-core/com.airwallex.android.core.model/-available-payment-method-type/index.md)&gt;, [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[PaymentConsent](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md)&gt;&gt;&gt;

Fetches available payment methods and consents. Should only be called once from the parent component.
