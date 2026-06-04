//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[NextAction](../index.md)/[DccData](index.md)

# DccData

[androidJvm]\
data class [DccData](index.md) : [AirwallexModel](../../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

The status of a [PaymentIntent](../../-payment-intent/index.md)

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? |
| [clientRate](client-rate.md) | [androidJvm]<br>val [clientRate](client-rate.md): [Double](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-double/index.html)? |
| [currency](currency.md) | [androidJvm]<br>val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [currencyPair](currency-pair.md) | [androidJvm]<br>val [currencyPair](currency-pair.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [rateExpiry](rate-expiry.md) | [androidJvm]<br>val [rateExpiry](rate-expiry.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [rateSource](rate-source.md) | [androidJvm]<br>val [rateSource](rate-source.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [rateTimestamp](rate-timestamp.md) | [androidJvm]<br>val [rateTimestamp](rate-timestamp.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
