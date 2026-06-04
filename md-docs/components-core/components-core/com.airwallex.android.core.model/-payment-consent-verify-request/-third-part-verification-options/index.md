//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentConsentVerifyRequest](../index.md)/[ThirdPartVerificationOptions](index.md)

# ThirdPartVerificationOptions

[androidJvm]\
data class [ThirdPartVerificationOptions](index.md)(val flow: [AirwallexPaymentRequestFlow](../../-airwallex-payment-request-flow/index.md)? = null, val osType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) : [AirwallexRequestModel](../../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [ThirdPartVerificationOptions](-third-part-verification-options.md) | [androidJvm]<br>constructor(flow: [AirwallexPaymentRequestFlow](../../-airwallex-payment-request-flow/index.md)? = null, osType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [flow](flow.md) | [androidJvm]<br>val [flow](flow.md): [AirwallexPaymentRequestFlow](../../-airwallex-payment-request-flow/index.md)? = null<br>Refer to the specification of the verification_options of the payment method. |
| [osType](os-type.md) | [androidJvm]<br>val [osType](os-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Can be one of ios, android. osType must be set when flow is mweb, inapp. |
| [sdkType](../../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
