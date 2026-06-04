//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentIntent](../index.md)/[FailureDetails](index.md)

# FailureDetails

[androidJvm]\
data class [FailureDetails](index.md)(val code: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val details: [PaymentIntent.FailureDetails.Details](-details/index.md)?) : [AirwallexModel](../../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [FailureDetails](-failure-details.md) | [androidJvm]<br>constructor(code: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), details: [PaymentIntent.FailureDetails.Details](-details/index.md)?) |

## Types

| Name | Summary |
|---|---|
| [Details](-details/index.md) | [androidJvm]<br>data class [Details](-details/index.md)(val originalResponseCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val originalResponseMessage: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [AirwallexModel](../../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |

## Properties

| Name | Summary |
|---|---|
| [code](code.md) | [androidJvm]<br>val [code](code.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Failure code |
| [details](details.md) | [androidJvm]<br>val [details](details.md): [PaymentIntent.FailureDetails.Details](-details/index.md)?<br>Additional failure details |
| [message](message.md) | [androidJvm]<br>val [message](message.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Failure message |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
