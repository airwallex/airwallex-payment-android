//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[Bank](index.md)

# Bank

[androidJvm]\
data class [Bank](index.md)(val name: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val displayName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val resources: [BankResources](../-bank-resources/index.md)?) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Bank information.

## Constructors

| | |
|---|---|
| [Bank](-bank.md) | [androidJvm]<br>constructor(name: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), displayName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), resources: [BankResources](../-bank-resources/index.md)?) |

## Properties

| Name | Summary |
|---|---|
| [displayName](display-name.md) | [androidJvm]<br>val [displayName](display-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>display name of the bank |
| [name](name.md) | [androidJvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>name of the bank |
| [resources](resources.md) | [androidJvm]<br>val [resources](resources.md): [BankResources](../-bank-resources/index.md)?<br>logo of the bank |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
