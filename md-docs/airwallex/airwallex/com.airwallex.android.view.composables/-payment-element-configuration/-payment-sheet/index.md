//[airwallex](../../../../index.md)/[com.airwallex.android.view.composables](../../index.md)/[PaymentElementConfiguration](../index.md)/[PaymentSheet](index.md)

# PaymentSheet

data class [PaymentSheet](index.md)(val layout: [PaymentMethodsLayoutType](../../../../../components-core/components-core/com.airwallex.android.core/-payment-methods-layout-type/index.md) = PaymentMethodsLayoutType.TAB, val googlePayButton: [PaymentElementConfiguration.GooglePayButton](../-google-pay-button/index.md) = GooglePayButton(), val checkoutButton: [PaymentElementConfiguration.CheckoutButton](../-checkout-button/index.md) = CheckoutButton(), val appearance: [Appearance](../../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null) : [PaymentElementConfiguration](../index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Configuration for payment sheet with multiple payment methods. Can display in Tab or Accordion layout.

#### Parameters

androidJvm

| | |
|---|---|
| layout | The layout type - TAB or ACCORDION |
| googlePayButton | Google Pay button configuration |
| checkoutButton | Checkout button configuration |
| appearance | Payment UI appearance configuration (theme color and dark mode) |

## Constructors

| | |
|---|---|
| [PaymentSheet](-payment-sheet.md) | [androidJvm]<br>constructor(layout: [PaymentMethodsLayoutType](../../../../../components-core/components-core/com.airwallex.android.core/-payment-methods-layout-type/index.md) = PaymentMethodsLayoutType.TAB, googlePayButton: [PaymentElementConfiguration.GooglePayButton](../-google-pay-button/index.md) = GooglePayButton(), checkoutButton: [PaymentElementConfiguration.CheckoutButton](../-checkout-button/index.md) = CheckoutButton(), appearance: [Appearance](../../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null) |

## Properties

| Name | Summary |
|---|---|
| [appearance](appearance.md) | [androidJvm]<br>open override val [appearance](appearance.md): [Appearance](../../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null |
| [checkoutButton](checkout-button.md) | [androidJvm]<br>open override val [checkoutButton](checkout-button.md): [PaymentElementConfiguration.CheckoutButton](../-checkout-button/index.md) |
| [googlePayButton](google-pay-button.md) | [androidJvm]<br>val [googlePayButton](google-pay-button.md): [PaymentElementConfiguration.GooglePayButton](../-google-pay-button/index.md) |
| [layout](layout.md) | [androidJvm]<br>val [layout](layout.md): [PaymentMethodsLayoutType](../../../../../components-core/components-core/com.airwallex.android.core/-payment-methods-layout-type/index.md) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](index.md#-1578325224%2FFunctions%2F1201863744) | [androidJvm]<br>abstract fun [describeContents](index.md#-1578325224%2FFunctions%2F1201863744)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](index.md#-1754457655%2FFunctions%2F1201863744) | [androidJvm]<br>abstract fun [writeToParcel](index.md#-1754457655%2FFunctions%2F1201863744)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
