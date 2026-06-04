//[airwallex](../../../index.md)/[com.airwallex.android.view.composables](../index.md)/[PaymentElementCallback](index.md)

# PaymentElementCallback

[androidJvm]\
interface [PaymentElementCallback](index.md)

Callback interface for PaymentElement creation results.

Implement this interface to receive callbacks when PaymentElement creation succeeds or fails. This is primarily for Java interoperability.

## Functions

| Name | Summary |
|---|---|
| [onFailure](on-failure.md) | [androidJvm]<br>abstract fun [onFailure](on-failure.md)(error: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html))<br>Called when PaymentElement creation fails. |
| [onSuccess](on-success.md) | [androidJvm]<br>abstract fun [onSuccess](on-success.md)(element: [PaymentElement](../-payment-element/index.md))<br>Called when PaymentElement is successfully created. |
