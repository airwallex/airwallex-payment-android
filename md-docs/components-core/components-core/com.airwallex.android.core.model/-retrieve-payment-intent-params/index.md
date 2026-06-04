//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[RetrievePaymentIntentParams](index.md)

# RetrievePaymentIntentParams

[androidJvm]\
data class [RetrievePaymentIntentParams](index.md)(val paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [AbstractPaymentIntentParams](../-abstract-payment-intent-params/index.md)

The params that used for retrieve [PaymentIntent](../-payment-intent/index.md)

## Constructors

| | |
|---|---|
| [RetrievePaymentIntentParams](-retrieve-payment-intent-params.md) | [androidJvm]<br>constructor(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [clientSecret](client-secret.md) | [androidJvm]<br>open override val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>The client secret of [PaymentIntent](../-payment-intent/index.md) |
| [paymentIntentId](payment-intent-id.md) | [androidJvm]<br>open override val [paymentIntentId](payment-intent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>ID of [PaymentIntent](../-payment-intent/index.md) |
