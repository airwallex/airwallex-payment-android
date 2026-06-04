//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[createPaymentMethod](create-payment-method.md)

# createPaymentMethod

[androidJvm]\

@[UiThread](https://developer.android.com/reference/kotlin/androidx/annotation/UiThread.html)

fun [createPaymentMethod](create-payment-method.md)(params: [CreatePaymentMethodParams](../../com.airwallex.android.core.model/-create-payment-method-params/index.md), listener: [Airwallex.PaymentListener](-payment-listener/index.md)&lt;[PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md)&gt;)

Create a payment method

#### Parameters

androidJvm

| | |
|---|---|
| params | [CreatePaymentMethodParams](../../com.airwallex.android.core.model/-create-payment-method-params/index.md) used to create the [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md) |
| listener | the callback of create [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md) |

[androidJvm]\
suspend fun [createPaymentMethod](create-payment-method.md)(params: [CreatePaymentMethodParams](../../com.airwallex.android.core.model/-create-payment-method-params/index.md)): [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md)
