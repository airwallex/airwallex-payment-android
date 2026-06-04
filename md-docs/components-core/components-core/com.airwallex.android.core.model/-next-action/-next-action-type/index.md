//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[NextAction](../index.md)/[NextActionType](index.md)

# NextActionType

[androidJvm]\
enum [NextActionType](index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[NextAction.NextActionType](index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

The status of a [PaymentIntent](../../-payment-intent/index.md)

## Entries

| | |
|---|---|
| [RENDER_QR_CODE](-r-e-n-d-e-r_-q-r_-c-o-d-e/index.md) | [androidJvm]<br>[RENDER_QR_CODE](-r-e-n-d-e-r_-q-r_-c-o-d-e/index.md) |
| [CALL_SDK](-c-a-l-l_-s-d-k/index.md) | [androidJvm]<br>[CALL_SDK](-c-a-l-l_-s-d-k/index.md) |
| [REDIRECT](-r-e-d-i-r-e-c-t/index.md) | [androidJvm]<br>[REDIRECT](-r-e-d-i-r-e-c-t/index.md) |
| [REDIRECT_FORM](-r-e-d-i-r-e-c-t_-f-o-r-m/index.md) | [androidJvm]<br>[REDIRECT_FORM](-r-e-d-i-r-e-c-t_-f-o-r-m/index.md) |
| [DISPLAY](-d-i-s-p-l-a-y/index.md) | [androidJvm]<br>[DISPLAY](-d-i-s-p-l-a-y/index.md) |
| [DCC](-d-c-c/index.md) | [androidJvm]<br>[DCC](-d-c-c/index.md) |

## Properties

| Name | Summary |
|---|---|
| [entries](entries.md) | [androidJvm]<br>val [entries](entries.md): [EnumEntries](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.enums/-enum-entries/index.html)&lt;[NextAction.NextActionType](index.md)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [name](../../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-372974862%2FProperties%2F1424399983) | [androidJvm]<br>val [name](../../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-372974862%2FProperties%2F1424399983): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [ordinal](../../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-739389684%2FProperties%2F1424399983) | [androidJvm]<br>val [ordinal](../../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-739389684%2FProperties%2F1424399983): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [value](value.md) | [androidJvm]<br>val [value](value.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [valueOf](value-of.md) | [androidJvm]<br>fun [valueOf](value-of.md)(value: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [NextAction.NextActionType](index.md)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [androidJvm]<br>fun [values](values.md)(): [Array](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-array/index.html)&lt;[NextAction.NextActionType](index.md)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
