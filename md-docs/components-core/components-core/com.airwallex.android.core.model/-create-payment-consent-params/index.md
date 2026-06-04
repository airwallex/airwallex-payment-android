//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[CreatePaymentConsentParams](index.md)

# CreatePaymentConsentParams

[androidJvm]\
data class [CreatePaymentConsentParams](index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val paymentMethodId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val googlePay: [PaymentMethod.GooglePay](../-payment-method/-google-pay/index.md)? = null, val paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md), val merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../-payment-consent/-merchant-trigger-reason/index.md)? = PaymentConsent.MerchantTriggerReason.UNSCHEDULED)

The params that used for create [PaymentConsent](../-payment-consent/index.md)

## Constructors

| | |
|---|---|
| [CreatePaymentConsentParams](-create-payment-consent-params.md) | [androidJvm]<br>constructor(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, googlePay: [PaymentMethod.GooglePay](../-payment-method/-google-pay/index.md)? = null, paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md), merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../-payment-consent/-merchant-trigger-reason/index.md)? = PaymentConsent.MerchantTriggerReason.UNSCHEDULED) |

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md)(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md)) : [ObjectBuilder](../-object-builder/index.md)&lt;[CreatePaymentConsentParams](index.md)&gt; |
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [clientSecret](client-secret.md) | [androidJvm]<br>val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [customerId](customer-id.md) | [androidJvm]<br>val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [googlePay](google-pay.md) | [androidJvm]<br>val [googlePay](google-pay.md): [PaymentMethod.GooglePay](../-payment-method/-google-pay/index.md)? = null<br>googlePay of the PaymentMethod attached for subsequent payments. Must be set when type is GooglePay. |
| [merchantTriggerReason](merchant-trigger-reason.md) | [androidJvm]<br>val [merchantTriggerReason](merchant-trigger-reason.md): [PaymentConsent.MerchantTriggerReason](../-payment-consent/-merchant-trigger-reason/index.md)?<br>Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled. Default: unscheduled |
| [nextTriggeredBy](next-triggered-by.md) | [androidJvm]<br>val [nextTriggeredBy](next-triggered-by.md): [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md)<br>The party to trigger subsequent payments. Can be one of merchant, customer. If type of payment_method is card, both merchant and customer is supported. Otherwise, only merchant is supported |
| [paymentMethodId](payment-method-id.md) | [androidJvm]<br>val [paymentMethodId](payment-method-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>ID of the PaymentMethod attached for subsequent payments. Must be set when type is card. |
| [paymentMethodType](payment-method-type.md) | [androidJvm]<br>val [paymentMethodType](payment-method-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Type of the PaymentMethod |
