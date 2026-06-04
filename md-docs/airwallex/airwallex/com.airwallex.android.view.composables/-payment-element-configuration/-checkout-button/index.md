//[airwallex](../../../../index.md)/[com.airwallex.android.view.composables](../../index.md)/[PaymentElementConfiguration](../index.md)/[CheckoutButton](index.md)

# CheckoutButton

[androidJvm]\
data class [CheckoutButton](index.md)(val title: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Checkout button configuration. Controls the payment confirmation button appearance and text.

## Constructors

| | |
|---|---|
| [CheckoutButton](-checkout-button.md) | [androidJvm]<br>constructor(title: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [title](title.md) | [androidJvm]<br>val [title](title.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Custom title for the checkout button. When null, defaults to &quot;Pay&quot; for one-off payments, &quot;Confirm&quot; for recurring payments. |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-payment-sheet/index.md#-1578325224%2FFunctions%2F1201863744) | [androidJvm]<br>abstract fun [describeContents](../-payment-sheet/index.md#-1578325224%2FFunctions%2F1201863744)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-payment-sheet/index.md#-1754457655%2FFunctions%2F1201863744) | [androidJvm]<br>abstract fun [writeToParcel](../-payment-sheet/index.md#-1754457655%2FFunctions%2F1201863744)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
