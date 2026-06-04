//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentIntentStatus](index.md)

# PaymentIntentStatus

[androidJvm]\
enum [PaymentIntentStatus](index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentIntentStatus](index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

The status of a [PaymentIntent](../-payment-intent/index.md)

## Entries

| | |
|---|---|
| [SUCCEEDED](-s-u-c-c-e-e-d-e-d/index.md) | [androidJvm]<br>[SUCCEEDED](-s-u-c-c-e-e-d-e-d/index.md)<br>The payment was successful, no further action required. |
| [CANCELLED](-c-a-n-c-e-l-l-e-d/index.md) | [androidJvm]<br>[CANCELLED](-c-a-n-c-e-l-l-e-d/index.md)<br>The payment intent has been cancelled. Uncaptured funds will be returned. |
| [REQUIRES_PAYMENT_METHOD](-r-e-q-u-i-r-e-s_-p-a-y-m-e-n-t_-m-e-t-h-o-d/index.md) | [androidJvm]<br>[REQUIRES_PAYMENT_METHOD](-r-e-q-u-i-r-e-s_-p-a-y-m-e-n-t_-m-e-t-h-o-d/index.md)<br>Populate `payment_method` when calling confirm This value is returned if `payment_method` is either null or the `payment_method` has failed during confirm, and a different `payment_method` should be provided. |
| [REQUIRES_CUSTOMER_ACTION](-r-e-q-u-i-r-e-s_-c-u-s-t-o-m-e-r_-a-c-t-i-o-n/index.md) | [androidJvm]<br>[REQUIRES_CUSTOMER_ACTION](-r-e-q-u-i-r-e-s_-c-u-s-t-o-m-e-r_-a-c-t-i-o-n/index.md)<br>Pending customer action, see `next_action` for details. Possible causes are pending 3DS authentication, QR code scan. |
| [REQUIRES_CAPTURE](-r-e-q-u-i-r-e-s_-c-a-p-t-u-r-e/index.md) | [androidJvm]<br>[REQUIRES_CAPTURE](-r-e-q-u-i-r-e-s_-c-a-p-t-u-r-e/index.md)<br>See `next_action` for the details. For example `next_action=capture` indicates that capture is outstanding. |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [entries](entries.md) | [androidJvm]<br>val [entries](entries.md): [EnumEntries](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.enums/-enum-entries/index.html)&lt;[PaymentIntentStatus](index.md)&gt;<br>Returns a representation of an immutable list of all enum entries, in the order they're declared. |
| [name](../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-372974862%2FProperties%2F1424399983) | [androidJvm]<br>val [name](../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-372974862%2FProperties%2F1424399983): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [ordinal](../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-739389684%2FProperties%2F1424399983) | [androidJvm]<br>val [ordinal](../-transaction-mode/-r-e-c-u-r-r-i-n-g/index.md#-739389684%2FProperties%2F1424399983): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [value](value.md) | [androidJvm]<br>val [value](value.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [valueOf](value-of.md) | [androidJvm]<br>fun [valueOf](value-of.md)(value: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [PaymentIntentStatus](index.md)<br>Returns the enum constant of this type with the specified name. The string must match exactly an identifier used to declare an enum constant in this type. (Extraneous whitespace characters are not permitted.) |
| [values](values.md) | [androidJvm]<br>fun [values](values.md)(): [Array](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-array/index.html)&lt;[PaymentIntentStatus](index.md)&gt;<br>Returns an array containing the constants of this enum type, in the order they're declared. |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
