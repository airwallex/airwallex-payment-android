//[airwallex](../../../index.md)/[com.airwallex.android.view](../index.md)/[PaymentFlowViewModel](index.md)/[updateSession](update-session.md)

# updateSession

[androidJvm]\
fun [updateSession](update-session.md)(newSession: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md))

Swap the session this VM operates on. The VM is Activity-scoped, so without this a second checkout on the same Activity would reuse the factory-injected session and confirm against an already-completed PaymentIntent. Resets per-session caches so PaymentSheet refetches methods/consents.
