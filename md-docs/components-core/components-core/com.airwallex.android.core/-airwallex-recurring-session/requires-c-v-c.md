//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexRecurringSession](index.md)/[requiresCVC](requires-c-v-c.md)

# requiresCVC

[androidJvm]\
val [requiresCVC](requires-c-v-c.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false

Only applicable when next_triggered_by is customer and the payment_method.type is card.If true, the customer must provide cvc for the subsequent payment with this PaymentConsent. Default: false
