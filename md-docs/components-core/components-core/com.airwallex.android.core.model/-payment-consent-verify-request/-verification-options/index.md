//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentConsentVerifyRequest](../index.md)/[VerificationOptions](index.md)

# VerificationOptions

[androidJvm]\
data class [VerificationOptions](index.md)(val type: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val cardOptions: [PaymentConsentVerifyRequest.CardVerificationOptions](../-card-verification-options/index.md)? = null, val thirdPartOptions: [PaymentConsentVerifyRequest.ThirdPartVerificationOptions](../-third-part-verification-options/index.md)? = null) : [AirwallexRequestModel](../../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [VerificationOptions](-verification-options.md) | [androidJvm]<br>constructor(type: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), cardOptions: [PaymentConsentVerifyRequest.CardVerificationOptions](../-card-verification-options/index.md)? = null, thirdPartOptions: [PaymentConsentVerifyRequest.ThirdPartVerificationOptions](../-third-part-verification-options/index.md)? = null) |

## Properties

| Name | Summary |
|---|---|
| [cardOptions](card-options.md) | [androidJvm]<br>val [cardOptions](card-options.md): [PaymentConsentVerifyRequest.CardVerificationOptions](../-card-verification-options/index.md)? = null<br>Card information for verifying PaymentConsent |
| [sdkType](../../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [thirdPartOptions](third-part-options.md) | [androidJvm]<br>val [thirdPartOptions](third-part-options.md): [PaymentConsentVerifyRequest.ThirdPartVerificationOptions](../-third-part-verification-options/index.md)? = null<br>Redirect information for verifying PaymentConsent. |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Type of the payment method |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
