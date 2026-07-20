//[airwallex](../../index.md)/[com.airwallex.android.view.util](index.md)/[resolvedBrandName](resolved-brand-name.md)

# resolvedBrandName

[androidJvm]\
fun [PaymentMethod.Card](../../../components-core/components-core/com.airwallex.android.core.model/-payment-method/-card/index.md).[resolvedBrandName](resolved-brand-name.md)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?

Resolves the brand name to display for a saved card. The backend labels Maestro cards as &quot;mastercard&quot;, so when the stored brand is Mastercard and the BIN identifies a Maestro card, surface Maestro instead. Every other brand is returned unchanged.
