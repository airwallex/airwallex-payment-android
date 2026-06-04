//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentMethod](index.md)

# PaymentMethod

[androidJvm]\
data class [PaymentMethod](index.md) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

A PaymentMethod represents the funding source that is used by your customer when making a payment. You may create and add multiple payment methods to a customer as saved payment methods to help streamline your customers' checkout experience.

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../-object-builder/index.md)&lt;[PaymentMethod](index.md)&gt; |
| [Card](-card/index.md) | [androidJvm]<br>data class [Card](-card/index.md) : [AirwallexModel](../-airwallex-model/index.md), [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |
| [GooglePay](-google-pay/index.md) | [androidJvm]<br>data class [GooglePay](-google-pay/index.md) : [AirwallexModel](../-airwallex-model/index.md), [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |
| [PaymentMethodStatus](-payment-method-status/index.md) | [androidJvm]<br>enum [PaymentMethodStatus](-payment-method-status/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentMethod.PaymentMethodStatus](-payment-method-status/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>The status of a [PaymentMethod](index.md) |

## Properties

| Name | Summary |
|---|---|
| [billing](billing.md) | [androidJvm]<br>val [billing](billing.md): [Billing](../-billing/index.md)? = null<br>Billing information for the payment method |
| [card](card.md) | [androidJvm]<br>val [card](card.md): [PaymentMethod.Card](-card/index.md)? = null<br>Card information for the payment method |
| [createdAt](created-at.md) | [androidJvm]<br>val [createdAt](created-at.md): [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)? = null<br>Time at which the payment method was created |
| [customerId](customer-id.md) | [androidJvm]<br>val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Customer id for the payment method |
| [googlePay](google-pay.md) | [androidJvm]<br>val [googlePay](google-pay.md): [PaymentMethod.GooglePay](-google-pay/index.md)? = null<br>Google Pay information for the payment method |
| [id](id.md) | [androidJvm]<br>val [id](id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique identifier for the payment method |
| [metadata](metadata.md) | [androidJvm]<br>val [metadata](metadata.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;? = null<br>A set of key-value pairs that you can attach to the payment method |
| [requestId](request-id.md) | [androidJvm]<br>val [requestId](request-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Request id for the payment method |
| [status](status.md) | [androidJvm]<br>val [status](status.md): [PaymentMethod.PaymentMethodStatus](-payment-method-status/index.md)? = null<br>Status of the payment method, can be one of CREATED, VERIFIED, EXPIRED, INVALID |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Type of the payment method. |
| [updatedAt](updated-at.md) | [androidJvm]<br>val [updatedAt](updated-at.md): [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)? = null<br>Last time at which the payment method was updated |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
