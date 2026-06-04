//[airwallex](../../../../index.md)/[com.airwallex.android.view.composables](../../index.md)/[PaymentElementConfiguration](../index.md)/[GooglePayButton](index.md)

# GooglePayButton

[androidJvm]\
data class [GooglePayButton](index.md)(val showsAsPrimaryButton: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true, val buttonType: ButtonType? = null) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Google Pay button configuration. Controls how the Google Pay button is displayed in the payment sheet.

## Constructors

| | |
|---|---|
| [GooglePayButton](-google-pay-button.md) | [androidJvm]<br>constructor(showsAsPrimaryButton: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true, buttonType: ButtonType? = null) |

## Properties

| Name | Summary |
|---|---|
| [buttonType](button-type.md) | [androidJvm]<br>val [buttonType](button-type.md): ButtonType? = null<br>The Google Pay button type (BUY, SUBSCRIBE, etc.). When null, defaults to BUY for one-off payments, SUBSCRIBE for recurring payments. |
| [showsAsPrimaryButton](shows-as-primary-button.md) | [androidJvm]<br>val [showsAsPrimaryButton](shows-as-primary-button.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true<br>If true, Google Pay is shown as a standalone button on top of the payment sheet. If false, Google Pay appears as a regular payment method option. |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-payment-sheet/index.md#-1578325224%2FFunctions%2F1201863744) | [androidJvm]<br>abstract fun [describeContents](../-payment-sheet/index.md#-1578325224%2FFunctions%2F1201863744)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-payment-sheet/index.md#-1754457655%2FFunctions%2F1201863744) | [androidJvm]<br>abstract fun [writeToParcel](../-payment-sheet/index.md#-1754457655%2FFunctions%2F1201863744)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
