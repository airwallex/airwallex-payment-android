//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[CreatePaymentConsentParams](../index.md)/[Companion](index.md)

# Companion

[androidJvm]\
object [Companion](index.md)

## Functions

| Name | Summary |
|---|---|
| [createCardParams](create-card-params.md) | [androidJvm]<br>fun [createCardParams](create-card-params.md)(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../../-payment-consent/-next-triggered-by/index.md), merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../../-payment-consent/-merchant-trigger-reason/index.md)?): [CreatePaymentConsentParams](../index.md) |
| [createGooglePayParams](create-google-pay-params.md) | [androidJvm]<br>fun [createGooglePayParams](create-google-pay-params.md)(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), googlePay: [PaymentMethod.GooglePay](../../-payment-method/-google-pay/index.md)?, nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../../-payment-consent/-next-triggered-by/index.md), merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../../-payment-consent/-merchant-trigger-reason/index.md)?): [CreatePaymentConsentParams](../index.md) |
| [createThirdPartParams](create-third-part-params.md) | [androidJvm]<br>fun [createThirdPartParams](create-third-part-params.md)(paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [CreatePaymentConsentParams](../index.md) |
