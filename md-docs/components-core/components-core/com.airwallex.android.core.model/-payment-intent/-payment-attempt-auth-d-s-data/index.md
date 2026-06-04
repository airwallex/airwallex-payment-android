//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentIntent](../index.md)/[PaymentAttemptAuthDSData](index.md)

# PaymentAttemptAuthDSData

[androidJvm]\
data class [PaymentAttemptAuthDSData](index.md) : [AirwallexModel](../../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Properties

| Name | Summary |
|---|---|
| [cavv](cavv.md) | [androidJvm]<br>val [cavv](cavv.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The Cavv. |
| [challengeCancellationReason](challenge-cancellation-reason.md) | [androidJvm]<br>val [challengeCancellationReason](challenge-cancellation-reason.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [eci](eci.md) | [androidJvm]<br>val [eci](eci.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The Eci. |
| [enrolled](enrolled.md) | [androidJvm]<br>val [enrolled](enrolled.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>Status of Authentication eligibility. |
| [frictionless](frictionless.md) | [androidJvm]<br>val [frictionless](frictionless.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [liabilityShiftIndicator](liability-shift-indicator.md) | [androidJvm]<br>val [liabilityShiftIndicator](liability-shift-indicator.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [paResStatus](pa-res-status.md) | [androidJvm]<br>val [paResStatus](pa-res-status.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>Transaction status result identifier. |
| [version](version.md) | [androidJvm]<br>val [version](version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>3DS Version |
| [xid](xid.md) | [androidJvm]<br>val [xid](xid.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The Xid. |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
