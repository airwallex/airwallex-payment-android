//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentMethodRequest](index.md)

# PaymentMethodRequest

[androidJvm]\
class [PaymentMethodRequest](index.md)(val id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val type: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val paymentRequest: [AirwallexPaymentRequest](../-airwallex-payment-request/index.md)? = null, val card: [PaymentMethod.Card](../-payment-method/-card/index.md)? = null, val googlePay: [PaymentMethod.GooglePay](../-payment-method/-google-pay/index.md)? = null, val billing: [Billing](../-billing/index.md)? = null) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [PaymentMethodRequest](-payment-method-request.md) | [androidJvm]<br>constructor(id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, type: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentRequest: [AirwallexPaymentRequest](../-airwallex-payment-request/index.md)? = null, card: [PaymentMethod.Card](../-payment-method/-card/index.md)? = null, googlePay: [PaymentMethod.GooglePay](../-payment-method/-google-pay/index.md)? = null, billing: [Billing](../-billing/index.md)? = null) |

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md)(val type: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [ObjectBuilder](../-object-builder/index.md)&lt;[PaymentMethodRequest](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [billing](billing.md) | [androidJvm]<br>val [billing](billing.md): [Billing](../-billing/index.md)? = null<br>Billing information for the payment method |
| [card](card.md) | [androidJvm]<br>val [card](card.md): [PaymentMethod.Card](../-payment-method/-card/index.md)? = null<br>Card information for the payment method |
| [googlePay](google-pay.md) | [androidJvm]<br>val [googlePay](google-pay.md): [PaymentMethod.GooglePay](../-payment-method/-google-pay/index.md)? = null<br>Google Pay information for the payment method |
| [id](id.md) | [androidJvm]<br>val [id](id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique identifier for the payment method |
| [paymentRequest](payment-request.md) | [androidJvm]<br>val [paymentRequest](payment-request.md): [AirwallexPaymentRequest](../-airwallex-payment-request/index.md)? = null<br>Payment Request |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Type of the payment method |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
