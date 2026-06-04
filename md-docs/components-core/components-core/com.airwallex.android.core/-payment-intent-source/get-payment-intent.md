//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[PaymentIntentSource](index.md)/[getPaymentIntent](get-payment-intent.md)

# getPaymentIntent

[androidJvm]\
abstract suspend fun [getPaymentIntent](get-payment-intent.md)(): [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)

Retrieves a PaymentIntent using suspend functions. This method should perform any necessary API calls or business logic to create and return a PaymentIntent.

#### Return

The PaymentIntent

#### Throws

| | |
|---|---|
| [Exception](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-exception/index.html) | if unable to retrieve the PaymentIntent |
