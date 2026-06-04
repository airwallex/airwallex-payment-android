//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[ContinuePaymentIntentParams](index.md)

# ContinuePaymentIntentParams

[androidJvm]\
data class [ContinuePaymentIntentParams](index.md)(val paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val type: [PaymentIntentContinueType](../-payment-intent-continue-type/index.md), val threeDSecure: [ThreeDSecure](../-three-d-secure/index.md)? = null, val device: [Device](../-device/index.md)? = null, val useDcc: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null) : [AbstractPaymentIntentParams](../-abstract-payment-intent-params/index.md)

The params that used for continue [PaymentIntent](../-payment-intent/index.md)

## Constructors

| | |
|---|---|
| [ContinuePaymentIntentParams](-continue-payment-intent-params.md) | [androidJvm]<br>constructor(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), type: [PaymentIntentContinueType](../-payment-intent-continue-type/index.md), threeDSecure: [ThreeDSecure](../-three-d-secure/index.md)? = null, device: [Device](../-device/index.md)? = null, useDcc: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [clientSecret](client-secret.md) | [androidJvm]<br>open override val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>The client secret of [PaymentIntent](../-payment-intent/index.md) |
| [device](device.md) | [androidJvm]<br>val [device](device.md): [Device](../-device/index.md)? = null |
| [paymentIntentId](payment-intent-id.md) | [androidJvm]<br>open override val [paymentIntentId](payment-intent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>ID of [PaymentIntent](../-payment-intent/index.md) |
| [threeDSecure](three-d-secure.md) | [androidJvm]<br>val [threeDSecure](three-d-secure.md): [ThreeDSecure](../-three-d-secure/index.md)? = null |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [PaymentIntentContinueType](../-payment-intent-continue-type/index.md) |
| [useDcc](use-dcc.md) | [androidJvm]<br>val [useDcc](use-dcc.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null |
