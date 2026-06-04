//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[ConfirmPaymentIntentParams](../index.md)/[Builder](index.md)

# Builder

[androidJvm]\
class [Builder](index.md)(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [ObjectBuilder](../../-object-builder/index.md)&lt;[ConfirmPaymentIntentParams](../index.md)&gt;

## Constructors

| | |
|---|---|
| [Builder](-builder.md) | [androidJvm]<br>constructor(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [androidJvm]<br>open override fun [build](build.md)(): [ConfirmPaymentIntentParams](../index.md) |
| [setAdditionalInfo](set-additional-info.md) | [androidJvm]<br>fun [setAdditionalInfo](set-additional-info.md)(additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setAutoCapture](set-auto-capture.md) | [androidJvm]<br>fun [setAutoCapture](set-auto-capture.md)(autoCapture: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setCountryCode](set-country-code.md) | [androidJvm]<br>fun [setCountryCode](set-country-code.md)(countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setCurrency](set-currency.md) | [androidJvm]<br>fun [setCurrency](set-currency.md)(currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setCustomerId](set-customer-id.md) | [androidJvm]<br>fun [setCustomerId](set-customer-id.md)(customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setCVC](set-c-v-c.md) | [androidJvm]<br>fun [setCVC](set-c-v-c.md)(cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setFlow](set-flow.md) | [androidJvm]<br>fun [setFlow](set-flow.md)(flow: [AirwallexPaymentRequestFlow](../../-airwallex-payment-request-flow/index.md)?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setPaymentConsentId](set-payment-consent-id.md) | [androidJvm]<br>fun [setPaymentConsentId](set-payment-consent-id.md)(paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setPaymentConsentOptions](set-payment-consent-options.md) | [androidJvm]<br>fun [setPaymentConsentOptions](set-payment-consent-options.md)(paymentConsentOptions: [PaymentConsentOptions](../../-payment-consent-options/index.md)?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setPaymentMethod](set-payment-method.md) | [androidJvm]<br>fun [setPaymentMethod](set-payment-method.md)(paymentMethod: [PaymentMethod](../../-payment-method/index.md)?): [ConfirmPaymentIntentParams.Builder](index.md) |
| [setReturnUrl](set-return-url.md) | [androidJvm]<br>fun [setReturnUrl](set-return-url.md)(returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [ConfirmPaymentIntentParams.Builder](index.md) |
