//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[ConfirmPaymentIntentParams](../index.md)/[Companion](index.md)/[createGooglePayParams](create-google-pay-params.md)

# createGooglePayParams

[androidJvm]\
fun [createGooglePayParams](create-google-pay-params.md)(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethod: [PaymentMethod](../../-payment-method/index.md)? = null, cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, paymentConsentOptions: [PaymentConsentOptions](../../-payment-consent-options/index.md)? = null, returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, autoCapture: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true): [ConfirmPaymentIntentParams](../index.md)

Return the [ConfirmPaymentIntentParams](../index.md) for Google Pay

#### Parameters

androidJvm

| | |
|---|---|
| paymentIntentId | the ID of the [PaymentIntent](../../-payment-intent/index.md), required. |
| clientSecret | the clientSecret of [PaymentIntent](../../-payment-intent/index.md), required. |
| paymentMethod | the object of the [PaymentMethod](../../-payment-method/index.md), required. |
| cvc | optional. |
| customerId | the customerId of [PaymentIntent](../../-payment-intent/index.md), optional. |
| paymentConsentId | the customerId of [PaymentConsent](../../-payment-consent/index.md), optional. |
| paymentConsentOptions | payment consent options for unified flow, optional. |
| returnUrl | optional |
