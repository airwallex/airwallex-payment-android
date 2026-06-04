//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentMethodOptions](index.md)

# PaymentMethodOptions

[androidJvm]\
data class [PaymentMethodOptions](index.md) : [AirwallexModel](../-airwallex-model/index.md), [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Options for payment method

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../-object-builder/index.md)&lt;[PaymentMethodOptions](index.md)&gt; |
| [CardOptions](-card-options/index.md) | [androidJvm]<br>data class [CardOptions](-card-options/index.md) : [AirwallexModel](../-airwallex-model/index.md), [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |

## Properties

| Name | Summary |
|---|---|
| [cardOptions](card-options.md) | [androidJvm]<br>val [cardOptions](card-options.md): [PaymentMethodOptions.CardOptions](-card-options/index.md)? = null<br>The payment method options for card |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
