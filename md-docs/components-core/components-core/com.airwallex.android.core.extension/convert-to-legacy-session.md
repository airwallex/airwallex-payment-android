//[components-core](../../index.md)/[com.airwallex.android.core.extension](index.md)/[convertToLegacySession](convert-to-legacy-session.md)

# convertToLegacySession

[androidJvm]\
suspend fun [Session](../com.airwallex.android.core/-session/index.md).[convertToLegacySession](convert-to-legacy-session.md)(): [AirwallexSession](../com.airwallex.android.core/-airwallex-session/index.md)

Converts the current [Session](../com.airwallex.android.core/-session/index.md) instance to a legacy [AirwallexSession](../com.airwallex.android.core/-airwallex-session/index.md) object.

This conversion is primarily required for Local Payment Methods (LPM), as they are not yet supported by the unified Session flow.

Determines which legacy session to create based on Session properties:

- 
   `paymentConsentOptions == null` → [AirwallexPaymentSession](../com.airwallex.android.core/-airwallex-payment-session/index.md) (one-off)
- 
   `paymentConsentOptions != null && amount == 0` → [AirwallexRecurringSession](../com.airwallex.android.core/-airwallex-recurring-session/index.md)
- 
   `paymentConsentOptions != null && amount > 0` → [AirwallexRecurringWithIntentSession](../com.airwallex.android.core/-airwallex-recurring-with-intent-session/index.md)

Preserves PaymentIntentProvider for Express Checkout scenarios where applicable.

#### Return

A legacy [AirwallexSession](../com.airwallex.android.core/-airwallex-session/index.md) object representing the current session state

#### Throws

| | |
|---|---|
| [IllegalArgumentException](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-illegal-argument-exception/index.html) | if required properties are missing |
