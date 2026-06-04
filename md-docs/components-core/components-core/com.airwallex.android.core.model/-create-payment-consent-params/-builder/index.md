//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[CreatePaymentConsentParams](../index.md)/[Builder](index.md)

# Builder

[androidJvm]\
class [Builder](index.md)(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../../-payment-consent/-next-triggered-by/index.md)) : [ObjectBuilder](../../-object-builder/index.md)&lt;[CreatePaymentConsentParams](../index.md)&gt;

## Constructors

| | |
|---|---|
| [Builder](-builder.md) | [androidJvm]<br>constructor(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../../-payment-consent/-next-triggered-by/index.md)) |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [androidJvm]<br>open override fun [build](build.md)(): [CreatePaymentConsentParams](../index.md) |
| [setGooglePay](set-google-pay.md) | [androidJvm]<br>fun [setGooglePay](set-google-pay.md)(googlePay: [PaymentMethod.GooglePay](../../-payment-method/-google-pay/index.md)?): [CreatePaymentConsentParams.Builder](index.md) |
| [setMerchantTriggerReason](set-merchant-trigger-reason.md) | [androidJvm]<br>fun [setMerchantTriggerReason](set-merchant-trigger-reason.md)(merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../../-payment-consent/-merchant-trigger-reason/index.md)?): [CreatePaymentConsentParams.Builder](index.md) |
| [setPaymentMethodId](set-payment-method-id.md) | [androidJvm]<br>fun [setPaymentMethodId](set-payment-method-id.md)(paymentMethodId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [CreatePaymentConsentParams.Builder](index.md) |
