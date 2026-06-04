//[components-core](../../../../../index.md)/[com.airwallex.android.core.model](../../../index.md)/[PaymentIntent](../../index.md)/[FailureDetails](../index.md)/[Details](index.md)

# Details

[androidJvm]\
data class [Details](index.md)(val originalResponseCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val originalResponseMessage: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [AirwallexModel](../../../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [Details](-details.md) | [androidJvm]<br>constructor(originalResponseCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, originalResponseMessage: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |

## Properties

| Name | Summary |
|---|---|
| [originalResponseCode](original-response-code.md) | [androidJvm]<br>val [originalResponseCode](original-response-code.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>Original response code from payment provider |
| [originalResponseMessage](original-response-message.md) | [androidJvm]<br>val [originalResponseMessage](original-response-message.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>Original response message from payment provider |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
