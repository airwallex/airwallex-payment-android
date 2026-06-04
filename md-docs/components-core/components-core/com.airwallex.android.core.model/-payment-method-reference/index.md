//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentMethodReference](index.md)

# PaymentMethodReference

[androidJvm]\
data class [PaymentMethodReference](index.md)(val id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [AirwallexModel](../-airwallex-model/index.md), [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Reference for payment method

## Constructors

| | |
|---|---|
| [PaymentMethodReference](-payment-method-reference.md) | [androidJvm]<br>constructor(id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) |

## Properties

| Name | Summary |
|---|---|
| [cvc](cvc.md) | [androidJvm]<br>val [cvc](cvc.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>The cvc of the card |
| [id](id.md) | [androidJvm]<br>val [id](id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>The id of the [PaymentMethod](../-payment-method/index.md) |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
