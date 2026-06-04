//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentIntentContinueRequest](index.md)

# PaymentIntentContinueRequest

[androidJvm]\
data class [PaymentIntentContinueRequest](index.md)(val requestId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val type: [PaymentIntentContinueType](../-payment-intent-continue-type/index.md)? = null, val threeDSecure: [ThreeDSecure](../-three-d-secure/index.md)? = null, val device: [Device](../-device/index.md)? = null, val useDcc: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

The request params to confirm [PaymentIntent](../-payment-intent/index.md)

## Constructors

| | |
|---|---|
| [PaymentIntentContinueRequest](-payment-intent-continue-request.md) | [androidJvm]<br>constructor(requestId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, type: [PaymentIntentContinueType](../-payment-intent-continue-type/index.md)? = null, threeDSecure: [ThreeDSecure](../-three-d-secure/index.md)? = null, device: [Device](../-device/index.md)? = null, useDcc: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [device](device.md) | [androidJvm]<br>val [device](device.md): [Device](../-device/index.md)? = null |
| [requestId](request-id.md) | [androidJvm]<br>val [requestId](request-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique request ID specified by the merchant |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [threeDSecure](three-d-secure.md) | [androidJvm]<br>val [threeDSecure](three-d-secure.md): [ThreeDSecure](../-three-d-secure/index.md)? = null<br>3D Secure |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [PaymentIntentContinueType](../-payment-intent-continue-type/index.md)? = null<br>3D Secure Type |
| [useDcc](use-dcc.md) | [androidJvm]<br>val [useDcc](use-dcc.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
