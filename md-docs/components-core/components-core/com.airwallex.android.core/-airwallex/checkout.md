//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[checkout](checkout.md)

# checkout

[androidJvm]\

@[UiThread](https://developer.android.com/reference/kotlin/androidx/annotation/UiThread.html)

fun [checkout](checkout.md)(session: [AirwallexSession](../-airwallex-session/index.md), paymentMethod: [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md), paymentConsent: [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)? = null, cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, flow: [AirwallexPaymentRequestFlow](../../com.airwallex.android.core.model/-airwallex-payment-request-flow/index.md)? = null, listener: [Airwallex.PaymentResultListener](-payment-result-listener/index.md), saveCard: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false)

Checkout the payment. This should be the entry point to handle all checkout cases

#### Parameters

androidJvm

| | |
|---|---|
| session | a [AirwallexSession](../-airwallex-session/index.md) used to present the Checkout flow, required. |
| paymentMethod | a [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md) used to present the Checkout flow, required. |
| paymentConsent | a [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md) object used for the payment, optional. Must have a valid ID if provided. |
| cvc | the CVC of the Credit Card, optional. |
| additionalInfo | used by LPMs |
| flow | an [AirwallexPaymentRequestFlow](../../com.airwallex.android.core.model/-airwallex-payment-request-flow/index.md), currently only supporting [AirwallexPaymentRequestFlow.IN_APP](../../com.airwallex.android.core.model/-airwallex-payment-request-flow/-i-n_-a-p-p/index.md), optional. |
| listener | The callback of checkout |
| saveCard | whether card will be saved as a payment consent, optional. |
