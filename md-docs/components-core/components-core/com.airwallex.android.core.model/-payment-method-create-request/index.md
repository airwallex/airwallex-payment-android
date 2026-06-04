//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentMethodCreateRequest](index.md)

# PaymentMethodCreateRequest

[androidJvm]\
data class [PaymentMethodCreateRequest](index.md) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Params for create a payment method

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../-object-builder/index.md)&lt;[PaymentMethodCreateRequest](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [billing](billing.md) | [androidJvm]<br>val [billing](billing.md): [Billing](../-billing/index.md)? = null<br>Billing information. |
| [card](card.md) | [androidJvm]<br>val [card](card.md): [PaymentMethod.Card](../-payment-method/-card/index.md)? = null<br>Card information. This must be provided if [type](type.md) is set to [PaymentMethodType.CARD](../-payment-method-type/-c-a-r-d/index.md) |
| [customerId](customer-id.md) | [androidJvm]<br>val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The customer this payment method belongs to. If set, this payment method is automatically added to the customer as one of the available payment methods. |
| [metadata](metadata.md) | [androidJvm]<br>val [metadata](metadata.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt;? = null<br>A set of key-value pairs that you can attach to this payment method |
| [requestId](request-id.md) | [androidJvm]<br>val [requestId](request-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique request ID specified by the merchant |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [PaymentMethodType](../-payment-method-type/index.md)? = null<br>Type of the payment method. Must be [PaymentMethodType.CARD](../-payment-method-type/-c-a-r-d/index.md) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
