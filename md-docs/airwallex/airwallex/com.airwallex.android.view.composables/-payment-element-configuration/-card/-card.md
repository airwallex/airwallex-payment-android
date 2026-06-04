//[airwallex](../../../../index.md)/[com.airwallex.android.view.composables](../../index.md)/[PaymentElementConfiguration](../index.md)/[Card](index.md)/[Card](-card.md)

# Card

[androidJvm]\
constructor(supportedCardBrands: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexSupportedCard](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)&gt; = enumValues&lt;AirwallexSupportedCard&gt;().toList(), checkoutButton: [PaymentElementConfiguration.CheckoutButton](../-checkout-button/index.md) = CheckoutButton(), appearance: [Appearance](../../../../../components-core/components-core/com.airwallex.android.core/-appearance/index.md)? = null)

#### Parameters

androidJvm

| | |
|---|---|
| supportedCardBrands | List of supported card brands/schemes.     Defaults to all cards from [AirwallexSupportedCard](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)     (Visa, Amex, Mastercard, Discover, JCB, Diners Club, UnionPay). |
| checkoutButton | Checkout button configuration |
| appearance | Payment UI appearance configuration (theme color and dark mode) |
