//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentIntent](../index.md)/[PaymentAttemptAuthData](index.md)

# PaymentAttemptAuthData

[androidJvm]\
data class [PaymentAttemptAuthData](index.md)(val dsData: [PaymentIntent.PaymentAttemptAuthDSData](../-payment-attempt-auth-d-s-data/index.md)?, val fraudData: [PaymentIntent.PaymentAttemptAuthFraudData](../-payment-attempt-auth-fraud-data/index.md)?, val avsResult: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val cvcResult: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [AirwallexModel](../../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [PaymentAttemptAuthData](-payment-attempt-auth-data.md) | [androidJvm]<br>constructor(dsData: [PaymentIntent.PaymentAttemptAuthDSData](../-payment-attempt-auth-d-s-data/index.md)?, fraudData: [PaymentIntent.PaymentAttemptAuthFraudData](../-payment-attempt-auth-fraud-data/index.md)?, avsResult: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, cvcResult: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |

## Properties

| Name | Summary |
|---|---|
| [avsResult](avs-result.md) | [androidJvm]<br>val [avsResult](avs-result.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [cvcResult](cvc-result.md) | [androidJvm]<br>val [cvcResult](cvc-result.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [dsData](ds-data.md) | [androidJvm]<br>val [dsData](ds-data.md): [PaymentIntent.PaymentAttemptAuthDSData](../-payment-attempt-auth-d-s-data/index.md)? |
| [fraudData](fraud-data.md) | [androidJvm]<br>val [fraudData](fraud-data.md): [PaymentIntent.PaymentAttemptAuthFraudData](../-payment-attempt-auth-fraud-data/index.md)? |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
