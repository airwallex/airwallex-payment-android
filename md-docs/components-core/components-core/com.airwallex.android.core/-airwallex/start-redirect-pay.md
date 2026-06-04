//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[startRedirectPay](start-redirect-pay.md)

# startRedirectPay

[androidJvm]\

@[UiThread](https://developer.android.com/reference/kotlin/androidx/annotation/UiThread.html)

fun [startRedirectPay](start-redirect-pay.md)(session: [AirwallexSession](../-airwallex-session/index.md), paymentMethodName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, flow: [AirwallexPaymentRequestFlow](../../com.airwallex.android.core.model/-airwallex-payment-request-flow/index.md)? = AirwallexPaymentRequestFlow.IN_APP, listener: [Airwallex.PaymentResultListener](-payment-result-listener/index.md))

Checkout the payment by paymentType and session

#### Parameters

androidJvm

| | |
|---|---|
| session | a [AirwallexSession](../-airwallex-session/index.md) used to present the Checkout flow, required. |
| paymentMethodName | a [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) representing one of the redirect payment type names, required. check all methods by API reference: https://www.airwallex.com/docs/api#/Payment_Acceptance/Config/_api_v1_pa_config_payment_method_types/get JSON Object field: items.name |
| additionalInfo | a [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html) containing extra information needed for certain payment types, such as phone number, email, bank details, etc., optional. |
| flow | an [AirwallexPaymentRequestFlow](../../com.airwallex.android.core.model/-airwallex-payment-request-flow/index.md), currently only supporting [AirwallexPaymentRequestFlow.IN_APP](../../com.airwallex.android.core.model/-airwallex-payment-request-flow/-i-n_-a-p-p/index.md), optional. |
| listener | the callback for the checkout result. |
