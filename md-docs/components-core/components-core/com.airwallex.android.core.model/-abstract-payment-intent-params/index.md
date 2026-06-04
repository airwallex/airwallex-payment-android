//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[AbstractPaymentIntentParams](index.md)

# AbstractPaymentIntentParams

abstract class [AbstractPaymentIntentParams](index.md)(val paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html))

#### Inheritors

| |
|---|
| [ConfirmPaymentIntentParams](../-confirm-payment-intent-params/index.md) |
| [ContinuePaymentIntentParams](../-continue-payment-intent-params/index.md) |
| [RetrievePaymentIntentParams](../-retrieve-payment-intent-params/index.md) |

## Constructors

| | |
|---|---|
| [AbstractPaymentIntentParams](-abstract-payment-intent-params.md) | [androidJvm]<br>constructor(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [clientSecret](client-secret.md) | [androidJvm]<br>open val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>The client secret of [PaymentIntent](../-payment-intent/index.md) |
| [paymentIntentId](payment-intent-id.md) | [androidJvm]<br>open val [paymentIntentId](payment-intent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>ID of [PaymentIntent](../-payment-intent/index.md) |
