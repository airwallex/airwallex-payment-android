//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[BillingAddressParameters](index.md)

# BillingAddressParameters

[androidJvm]\
data class [BillingAddressParameters](index.md)(val format: [BillingAddressParameters.Format](-format/index.md)? = Format.MIN, val phoneNumberRequired: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = false) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [BillingAddressParameters](-billing-address-parameters.md) | [androidJvm]<br>constructor(format: [BillingAddressParameters.Format](-format/index.md)? = Format.MIN, phoneNumberRequired: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = false) |

## Types

| Name | Summary |
|---|---|
| [Format](-format/index.md) | [androidJvm]<br>enum [Format](-format/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[BillingAddressParameters.Format](-format/index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [format](format.md) | [androidJvm]<br>val [format](format.md): [BillingAddressParameters.Format](-format/index.md)? |
| [phoneNumberRequired](phone-number-required.md) | [androidJvm]<br>val [phoneNumberRequired](phone-number-required.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = false |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../com.airwallex.android.core.model/-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../com.airwallex.android.core.model/-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../../com.airwallex.android.core.model/-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../com.airwallex.android.core.model/-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
