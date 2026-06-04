//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[WeChat](index.md)

# WeChat

[androidJvm]\
data class [WeChat](index.md)(val appId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val partnerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val prepayId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val package: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val nonceStr: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val timestamp: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val sign: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

WeChat Pay Payments

## Constructors

| | |
|---|---|
| [WeChat](-we-chat.md) | [androidJvm]<br>constructor(appId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, partnerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, prepayId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, package: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, nonceStr: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, timestamp: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, sign: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |

## Properties

| Name | Summary |
|---|---|
| [appId](app-id.md) | [androidJvm]<br>val [appId](app-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [nonceStr](nonce-str.md) | [androidJvm]<br>val [nonceStr](nonce-str.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [package](package.md) | [androidJvm]<br>val [package](package.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [partnerId](partner-id.md) | [androidJvm]<br>val [partnerId](partner-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [prepayId](prepay-id.md) | [androidJvm]<br>val [prepayId](prepay-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [sign](sign.md) | [androidJvm]<br>val [sign](sign.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [timestamp](timestamp.md) | [androidJvm]<br>val [timestamp](timestamp.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |

## Functions

| Name | Summary |
|---|---|
| [describeContents](index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
