//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[ConfirmPaymentIntentParams](../index.md)/[Companion](index.md)/[createThirdPartPayParams](create-third-part-pay-params.md)

# createThirdPartPayParams

[androidJvm]\
fun [createThirdPartPayParams](create-third-part-pay-params.md)(paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, flow: [AirwallexPaymentRequestFlow](../../-airwallex-payment-request-flow/index.md)? = null): [ConfirmPaymentIntentParams](../index.md)

Return the [ConfirmPaymentIntentParams](../index.md) for ThirdPart Pay

#### Parameters

androidJvm

| | |
|---|---|
| paymentMethodType | Payment method type, required. |
| paymentIntentId | the ID of the [PaymentIntent](../../-payment-intent/index.md), required. |
| clientSecret | the clientSecret of [PaymentIntent](../../-payment-intent/index.md), required. |
| customerId | the customerId of [PaymentIntent](../../-payment-intent/index.md), optional. |
| paymentConsentId | the customerId of [PaymentConsent](../../-payment-consent/index.md), optional. |
| currency | amount currency |
| additionalInfo | used by LPMs |
| returnUrl | optional |
| flow | optional |
