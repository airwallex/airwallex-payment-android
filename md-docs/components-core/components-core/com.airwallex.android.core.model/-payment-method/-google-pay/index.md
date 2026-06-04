//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentMethod](../index.md)/[GooglePay](index.md)

# GooglePay

[androidJvm]\
data class [GooglePay](index.md) : [AirwallexModel](../../-airwallex-model/index.md), [AirwallexRequestModel](../../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../../-object-builder/index.md)&lt;[PaymentMethod.GooglePay](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [billing](billing.md) | [androidJvm]<br>val [billing](billing.md): [Billing](../../-billing/index.md)? = null<br>Billing information for the payment method |
| [encryptedPaymentToken](encrypted-payment-token.md) | [androidJvm]<br>val [encryptedPaymentToken](encrypted-payment-token.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Encrypted payment token depends on the payment data type. |
| [flow](flow.md) | [androidJvm]<br>val [flow](flow.md): [AirwallexPaymentRequestFlow](../../-airwallex-payment-request-flow/index.md)? = null |
| [paymentDataType](payment-data-type.md) | [androidJvm]<br>val [paymentDataType](payment-data-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Type of the payment data details. One of tokenized_card or encrypted_payment_token. Only encrypted_payment_token is currently supported. |
| [sdkType](../../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
