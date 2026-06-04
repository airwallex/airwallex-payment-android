//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentConsentVerifyRequest](index.md)

# PaymentConsentVerifyRequest

[androidJvm]\
data class [PaymentConsentVerifyRequest](index.md) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Params for create a [PaymentConsent](../-payment-consent/index.md)

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../-object-builder/index.md)&lt;[PaymentConsentVerifyRequest](index.md)&gt; |
| [CardVerificationOptions](-card-verification-options/index.md) | [androidJvm]<br>data class [CardVerificationOptions](-card-verification-options/index.md)(val amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |
| [ThirdPartVerificationOptions](-third-part-verification-options/index.md) | [androidJvm]<br>data class [ThirdPartVerificationOptions](-third-part-verification-options/index.md)(val flow: [AirwallexPaymentRequestFlow](../-airwallex-payment-request-flow/index.md)? = null, val osType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |
| [VerificationOptions](-verification-options/index.md) | [androidJvm]<br>data class [VerificationOptions](-verification-options/index.md)(val type: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val cardOptions: [PaymentConsentVerifyRequest.CardVerificationOptions](-card-verification-options/index.md)? = null, val thirdPartOptions: [PaymentConsentVerifyRequest.ThirdPartVerificationOptions](-third-part-verification-options/index.md)? = null) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |

## Properties

| Name | Summary |
|---|---|
| [device](device.md) | [androidJvm]<br>val [device](device.md): [Device](../-device/index.md)? = null<br>Device info |
| [integrationData](integration-data.md) | [androidJvm]<br>val [integrationData](integration-data.md): [IntegrationData](../-integration-data/index.md)? = null<br>Integration data |
| [requestId](request-id.md) | [androidJvm]<br>val [requestId](request-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique request ID specified by the merchant |
| [returnUrl](return-url.md) | [androidJvm]<br>val [returnUrl](return-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The URL to which your customer will be redirected after they verify PaymentConsent on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively provide an application URI scheme. |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [verificationOptions](verification-options.md) | [androidJvm]<br>val [verificationOptions](verification-options.md): [PaymentConsentVerifyRequest.VerificationOptions](-verification-options/index.md)? = null<br>Additional information needed to verify a PaymentConsent |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
