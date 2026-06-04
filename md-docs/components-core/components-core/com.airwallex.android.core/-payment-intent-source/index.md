//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[PaymentIntentSource](index.md)

# PaymentIntentSource

[androidJvm]\
interface [PaymentIntentSource](index.md)

Modern suspend-based interface for providing PaymentIntent objects. This is the preferred interface for Kotlin consumers as it provides cleaner async handling.

## Kotlin Implementation Example

```kotlin
class MyPaymentIntentSource(private val apiService: ApiService) : PaymentIntentSource {
    override suspend fun getPaymentIntent(): PaymentIntent {
        return apiService.createPaymentIntent()
    }
}
```

## Java Compatibility

If you need Java compatibility, use the callback-based [PaymentIntentProvider](../-payment-intent-provider/index.md) interface instead.

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>abstract val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)<br>Payment amount. This is the order amount you would like to charge your customer. Required for payment session creation. |
| [currency](currency.md) | [androidJvm]<br>abstract val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Amount currency. Required for payment session creation. |

## Functions

| Name | Summary |
|---|---|
| [getPaymentIntent](get-payment-intent.md) | [androidJvm]<br>abstract suspend fun [getPaymentIntent](get-payment-intent.md)(): [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)<br>Retrieves a PaymentIntent using suspend functions. This method should perform any necessary API calls or business logic to create and return a PaymentIntent. |
