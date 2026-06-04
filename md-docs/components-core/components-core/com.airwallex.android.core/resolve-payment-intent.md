//[components-core](../../index.md)/[com.airwallex.android.core](index.md)/[resolvePaymentIntent](resolve-payment-intent.md)

# resolvePaymentIntent

[androidJvm]\
fun [AirwallexSession](-airwallex-session/index.md).[resolvePaymentIntent](resolve-payment-intent.md)(callback: [PaymentIntentProvider.PaymentIntentCallback](-payment-intent-provider/-payment-intent-callback/index.md))

Extension function to resolve PaymentIntent from session. If paymentIntent is available, calls callback immediately. If paymentIntentProvider is available (transient field), uses it to get the intent asynchronously. If paymentIntentProviderId is available (after binding), retrieves from repository.

This function works with any session type that supports PaymentIntentProvider (AirwallexPaymentSession and AirwallexRecurringWithIntentSession).

#### Parameters

androidJvm

| | |
|---|---|
| callback | Callback to receive the PaymentIntent result |
