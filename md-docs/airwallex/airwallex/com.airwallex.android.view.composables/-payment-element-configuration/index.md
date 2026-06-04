//[airwallex](../../../index.md)/[com.airwallex.android.view.composables](../index.md)/[PaymentElementConfiguration](index.md)

# PaymentElementConfiguration

sealed class [PaymentElementConfiguration](index.md)

Configuration for Airwallex Payment Element.

Use [Card](-card/index.md) for standalone card payment UI. Use [PaymentSheet](-payment-sheet/index.md) for multi-payment method UI with Tab or Accordion layout.

#### Inheritors

| |
|---|
| [Card](-card/index.md) |
| [PaymentSheet](-payment-sheet/index.md) |

## Types

| Name | Summary |
|---|---|
| [Card](-card/index.md) | [androidJvm]<br>data class [Card](-card/index.md)(val supportedCardBrands: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexSupportedCard](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)&gt; = enumValues&lt;AirwallexSupportedCard&gt;().toList(), val checkoutButton: [PaymentElementConfiguration.CheckoutButton](-checkout-button/index.md) = CheckoutButton(), val appearance: [Appearance](../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null) : [PaymentElementConfiguration](index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>Configuration for standalone card payment element. Shows only card input and saved cards (if available). |
| [CheckoutButton](-checkout-button/index.md) | [androidJvm]<br>data class [CheckoutButton](-checkout-button/index.md)(val title: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>Checkout button configuration. Controls the payment confirmation button appearance and text. |
| [GooglePayButton](-google-pay-button/index.md) | [androidJvm]<br>data class [GooglePayButton](-google-pay-button/index.md)(val showsAsPrimaryButton: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true, val buttonType: ButtonType? = null) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>Google Pay button configuration. Controls how the Google Pay button is displayed in the payment sheet. |
| [PaymentSheet](-payment-sheet/index.md) | [androidJvm]<br>data class [PaymentSheet](-payment-sheet/index.md)(val layout: [PaymentMethodsLayoutType](../../../../components-core/components-core/com.airwallex.android.core/-payment-methods-layout-type/index.md) = PaymentMethodsLayoutType.TAB, val googlePayButton: [PaymentElementConfiguration.GooglePayButton](-google-pay-button/index.md) = GooglePayButton(), val checkoutButton: [PaymentElementConfiguration.CheckoutButton](-checkout-button/index.md) = CheckoutButton(), val appearance: [Appearance](../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null) : [PaymentElementConfiguration](index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>Configuration for payment sheet with multiple payment methods. Can display in Tab or Accordion layout. |

## Properties

| Name | Summary |
|---|---|
| [appearance](appearance.md) | [androidJvm]<br>abstract val [appearance](appearance.md): [Appearance](../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)?<br>Payment UI appearance configuration (theme color and dark mode). When null, uses default appearance. |
| [checkoutButton](checkout-button.md) | [androidJvm]<br>abstract val [checkoutButton](checkout-button.md): [PaymentElementConfiguration.CheckoutButton](-checkout-button/index.md)<br>Checkout button configuration |
