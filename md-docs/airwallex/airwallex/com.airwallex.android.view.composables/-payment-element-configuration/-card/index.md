//[airwallex](../../../../index.md)/[com.airwallex.android.view.composables](../../index.md)/[PaymentElementConfiguration](../index.md)/[Card](index.md)

# Card

data class [Card](index.md)(val supportedCardBrands: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexSupportedCard](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)&gt; = enumValues&lt;AirwallexSupportedCard&gt;().toList(), val checkoutButton: [PaymentElementConfiguration.CheckoutButton](../-checkout-button/index.md) = CheckoutButton(), val appearance: [Appearance](../../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null) : [PaymentElementConfiguration](../index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Configuration for standalone card payment element. Shows only card input and saved cards (if available).

#### Parameters

androidJvm

| | |
|---|---|
| supportedCardBrands | List of supported card brands/schemes.     Defaults to all cards from [AirwallexSupportedCard](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)     (Visa, Amex, Mastercard, Discover, JCB, Diners Club, UnionPay). |
| checkoutButton | Checkout button configuration |
| appearance | Payment UI appearance configuration (theme color and dark mode) |

## Constructors

| | |
|---|---|
| [Card](-card.md) | [androidJvm]<br>constructor(supportedCardBrands: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexSupportedCard](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)&gt; = enumValues&lt;AirwallexSupportedCard&gt;().toList(), checkoutButton: [PaymentElementConfiguration.CheckoutButton](../-checkout-button/index.md) = CheckoutButton(), appearance: [Appearance](../../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null) |

## Properties

| Name | Summary |
|---|---|
| [appearance](appearance.md) | [androidJvm]<br>open override val [appearance](appearance.md): [Appearance](../../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null |
| [checkoutButton](checkout-button.md) | [androidJvm]<br>open override val [checkoutButton](checkout-button.md): [PaymentElementConfiguration.CheckoutButton](../-checkout-button/index.md) |
| [supportedCardBrands](supported-card-brands.md) | [androidJvm]<br>val [supportedCardBrands](supported-card-brands.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexSupportedCard](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)&gt; |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-payment-sheet/index.md#-1578325224%2FFunctions%2F1201863744) | [androidJvm]<br>abstract fun [describeContents](../-payment-sheet/index.md#-1578325224%2FFunctions%2F1201863744)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-payment-sheet/index.md#-1754457655%2FFunctions%2F1201863744) | [androidJvm]<br>abstract fun [writeToParcel](../-payment-sheet/index.md#-1754457655%2FFunctions%2F1201863744)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
