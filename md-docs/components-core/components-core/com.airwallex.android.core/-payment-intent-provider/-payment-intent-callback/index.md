//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[PaymentIntentProvider](../index.md)/[PaymentIntentCallback](index.md)

# PaymentIntentCallback

[androidJvm]\
interface [PaymentIntentCallback](index.md)

Callback interface for receiving PaymentIntent results

## Functions

| Name | Summary |
|---|---|
| [onError](on-error.md) | [androidJvm]<br>abstract fun [onError](on-error.md)(error: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html))<br>Called when there's an error providing the PaymentIntent |
| [onSuccess](on-success.md) | [androidJvm]<br>abstract fun [onSuccess](on-success.md)(paymentIntent: [PaymentIntent](../../../com.airwallex.android.core.model/-payment-intent/index.md))<br>Called when PaymentIntent is successfully provided |
