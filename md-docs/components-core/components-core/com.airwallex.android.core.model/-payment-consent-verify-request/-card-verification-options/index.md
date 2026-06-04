//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentConsentVerifyRequest](../index.md)/[CardVerificationOptions](index.md)

# CardVerificationOptions

[androidJvm]\
data class [CardVerificationOptions](index.md)(val amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) : [AirwallexRequestModel](../../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [CardVerificationOptions](-card-verification-options.md) | [androidJvm]<br>constructor(amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null<br>The alternative amount of verification if zero amount is not acceptable for the provider. The transaction of this amount should be reverted once the verification process finished. Must be greater than 0. |
| [currency](currency.md) | [androidJvm]<br>val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Currency of the initial PaymentIntent to verify the PaymentConsent. Three-letter ISO currency code. Must be a supported currency |
| [cvc](cvc.md) | [androidJvm]<br>val [cvc](cvc.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>When requires_cvc for the PaymentConsent is true, this attribute must be provided in order to confirm successfully |
| [sdkType](../../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
