//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[PaymentIntentProvider](index.md)

# PaymentIntentProvider

[androidJvm]\
interface [PaymentIntentProvider](index.md)

Interface for providing PaymentIntent objects asynchronously. This allows consumer applications to provide payment intents on demand rather than having to provide them upfront when creating payment sessions.

## Simple Implementation Example

```kotlin
class MyPaymentProvider(private val apiKey: String, private val userId: String?) : PaymentIntentProvider {
    override fun provide(callback: PaymentIntentCallback) {
        // Your implementation here - any complexity, any properties
        // No need to worry about Parcelable or Serializable
    }
}
```

The SDK handles all the complexity of passing providers between activities automatically.

## Types

| Name | Summary |
|---|---|
| [PaymentIntentCallback](-payment-intent-callback/index.md) | [androidJvm]<br>interface [PaymentIntentCallback](-payment-intent-callback/index.md)<br>Callback interface for receiving PaymentIntent results |

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>abstract val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)<br>Payment amount. This is the order amount you would like to charge your customer. Required for payment session creation. |
| [currency](currency.md) | [androidJvm]<br>abstract val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Amount currency. Required for payment session creation. |

## Functions

| Name | Summary |
|---|---|
| [provide](provide.md) | [androidJvm]<br>abstract fun [provide](provide.md)(callback: [PaymentIntentProvider.PaymentIntentCallback](-payment-intent-callback/index.md))<br>Provides a PaymentIntent asynchronously. |
