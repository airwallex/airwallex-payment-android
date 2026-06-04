//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentMethodRequest](../index.md)/[Builder](index.md)

# Builder

[androidJvm]\
class [Builder](index.md)(val type: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [ObjectBuilder](../../-object-builder/index.md)&lt;[PaymentMethodRequest](../index.md)&gt;

## Constructors

| | |
|---|---|
| [Builder](-builder.md) | [androidJvm]<br>constructor(type: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [type](type.md) | [androidJvm]<br>val [type](type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [androidJvm]<br>open override fun [build](build.md)(): [PaymentMethodRequest](../index.md) |
| [setCardPaymentMethodRequest](set-card-payment-method-request.md) | [androidJvm]<br>fun [setCardPaymentMethodRequest](set-card-payment-method-request.md)(card: [PaymentMethod.Card](../../-payment-method/-card/index.md)?, billing: [Billing](../../-billing/index.md)?): [PaymentMethodRequest.Builder](index.md) |
| [setGooglePayPaymentMethodRequest](set-google-pay-payment-method-request.md) | [androidJvm]<br>fun [setGooglePayPaymentMethodRequest](set-google-pay-payment-method-request.md)(googlePay: [PaymentMethod.GooglePay](../../-payment-method/-google-pay/index.md)?): [PaymentMethodRequest.Builder](index.md) |
| [setId](set-id.md) | [androidJvm]<br>fun [setId](set-id.md)(id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [PaymentMethodRequest.Builder](index.md)<br>Set payment method ID for referencing existing payment methods |
| [setThirdPartyPaymentMethodRequest](set-third-party-payment-method-request.md) | [androidJvm]<br>fun [setThirdPartyPaymentMethodRequest](set-third-party-payment-method-request.md)(additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, flow: [AirwallexPaymentRequestFlow](../../-airwallex-payment-request-flow/index.md)? = null): [PaymentMethodRequest.Builder](index.md) |
