//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[TrackerRequest](../index.md)/[TrackerType](index.md)

# TrackerType

[androidJvm]\
enum [TrackerType](index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[TrackerRequest.TrackerType](index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Entries

| | |
|---|---|
| [CARD_NUMBER](-c-a-r-d_-n-u-m-b-e-r/index.md) | [androidJvm]<br>[CARD_NUMBER](-c-a-r-d_-n-u-m-b-e-r/index.md) |
| [EXPIRY](-e-x-p-i-r-y/index.md) | [androidJvm]<br>[EXPIRY](-e-x-p-i-r-y/index.md) |
| [CVC](-c-v-c/index.md) | [androidJvm]<br>[CVC](-c-v-c/index.md) |
| [PAYMENT_REQUEST_BUTTON](-p-a-y-m-e-n-t_-r-e-q-u-e-s-t_-b-u-t-t-o-n/index.md) | [androidJvm]<br>[PAYMENT_REQUEST_BUTTON](-p-a-y-m-e-n-t_-r-e-q-u-e-s-t_-b-u-t-t-o-n/index.md) |
| [CARD](-c-a-r-d/index.md) | [androidJvm]<br>[CARD](-c-a-r-d/index.md) |
| [WECHAT](-w-e-c-h-a-t/index.md) | [androidJvm]<br>[WECHAT](-w-e-c-h-a-t/index.md) |
| [QRCODE](-q-r-c-o-d-e/index.md) | [androidJvm]<br>[QRCODE](-q-r-c-o-d-e/index.md) |
| [REDIRECT](-r-e-d-i-r-e-c-t/index.md) | [androidJvm]<br>[REDIRECT](-r-e-d-i-r-e-c-t/index.md) |
| [DROP_IN](-d-r-o-p_-i-n/index.md) | [androidJvm]<br>[DROP_IN](-d-r-o-p_-i-n/index.md) |
| [FULL_FEATURED_CARD](-f-u-l-l_-f-e-a-t-u-r-e-d_-c-a-r-d/index.md) | [androidJvm]<br>[FULL_FEATURED_CARD](-f-u-l-l_-f-e-a-t-u-r-e-d_-c-a-r-d/index.md) |
| [HPP](-h-p-p/index.md) | [androidJvm]<br>[HPP](-h-p-p/index.md) |
| [REDIRECT_PAGE](-r-e-d-i-r-e-c-t_-p-a-g-e/index.md) | [androidJvm]<br>[REDIRECT_PAGE](-r-e-d-i-r-e-c-t_-p-a-g-e/index.md) |

## Properties

| Name | Summary |
|---|---|
| [entries](entries.md) | [androidJvm]<br>val [entries](entries.md): [EnumEntries](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.enums/-enum-entries/index.html)&lt;[TrackerRequest.TrackerType](index.md)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [name](../../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-372974862%2FProperties%2F1424399983) | [androidJvm]<br>val [name](../../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-372974862%2FProperties%2F1424399983): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [ordinal](../../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-739389684%2FProperties%2F1424399983) | [androidJvm]<br>val [ordinal](../../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-739389684%2FProperties%2F1424399983): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [value](value.md) | [androidJvm]<br>val [value](value.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [valueOf](value-of.md) | [androidJvm]<br>fun [valueOf](value-of.md)(value: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [TrackerRequest.TrackerType](index.md)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [androidJvm]<br>fun [values](values.md)(): [Array](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-array/index.html)&lt;[TrackerRequest.TrackerType](index.md)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
