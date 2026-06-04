//[components-core](../../index.md)/[com.airwallex.android.core.extension](index.md)/[convertToSession](convert-to-session.md)

# convertToSession

[androidJvm]\
fun [AirwallexPaymentSession](../com.airwallex.android.core/-airwallex-payment-session/index.md).[convertToSession](convert-to-session.md)(): [Session](../com.airwallex.android.core/-session/index.md)

Converts [AirwallexPaymentSession](../com.airwallex.android.core/-airwallex-payment-session/index.md) (legacy one-off payment session) to the new [Session](../com.airwallex.android.core/-session/index.md) type. Preserves both paymentIntent and paymentIntentProvider for full compatibility.

#### Return

A [Session](../com.airwallex.android.core/-session/index.md) object representing the one-off payment

[androidJvm]\
fun [AirwallexRecurringWithIntentSession](../com.airwallex.android.core/-airwallex-recurring-with-intent-session/index.md).[convertToSession](convert-to-session.md)(): [Session](../com.airwallex.android.core/-session/index.md)

Converts [AirwallexRecurringWithIntentSession](../com.airwallex.android.core/-airwallex-recurring-with-intent-session/index.md) (legacy recurring with intent session) to the new [Session](../com.airwallex.android.core/-session/index.md) type. Preserves both paymentIntent and paymentIntentProvider for full compatibility.

#### Return

A [Session](../com.airwallex.android.core/-session/index.md) object representing the recurring payment with intent
