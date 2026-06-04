//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[CreatePaymentMethodParams](index.md)

# CreatePaymentMethodParams

[androidJvm]\
data class [CreatePaymentMethodParams](index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val card: [PaymentMethod.Card](../-payment-method/-card/index.md), val billing: [Billing](../-billing/index.md)?) : [AbstractPaymentMethodParams](../-abstract-payment-method-params/index.md)

The params that used for create [PaymentMethod](../-payment-method/index.md)

## Constructors

| | |
|---|---|
| [CreatePaymentMethodParams](-create-payment-method-params.md) | [androidJvm]<br>constructor(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), card: [PaymentMethod.Card](../-payment-method/-card/index.md), billing: [Billing](../-billing/index.md)?) |

## Properties

| Name | Summary |
|---|---|
| [billing](billing.md) | [androidJvm]<br>val [billing](billing.md): [Billing](../-billing/index.md)?<br>The billing info of the [PaymentMethod](../-payment-method/index.md) |
| [card](card.md) | [androidJvm]<br>val [card](card.md): [PaymentMethod.Card](../-payment-method/-card/index.md)<br>The card info of the [PaymentMethod](../-payment-method/index.md) |
| [clientSecret](client-secret.md) | [androidJvm]<br>open override val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>The client secret of [PaymentIntent](../-payment-intent/index.md) |
| [customerId](customer-id.md) | [androidJvm]<br>open override val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>ID of Customer |
