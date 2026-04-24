# Migration Guide

## Migrating from version 6.6.1
- Changes to `AirwallexSession`:
    * `AirwallexPaymentSession`, `AirwallexRecurringSession`, and `AirwallexRecurringWithIntentSession` are deprecated, use the unified `Session` class instead
    * The flow is now determined by `PaymentConsentOptions` and the PaymentIntent `amount`:
        - one-off payment: no `PaymentConsentOptions`, `amount > 0`
        - recurring (consent only, no charge): set `PaymentConsentOptions`, `amount = 0`
        - recurring with intent (consent + charge): set `PaymentConsentOptions`, `amount > 0`
    * `customerId` is required on the session (or on the underlying `PaymentIntent`) for both recurring flows
    * `AirwallexSession.clientSecret` is now a property on the base class — use `session.clientSecret` instead of a helper
- Changes to `Airwallex`:
    * `confirmPaymentIntent(session, card, billing, saveCard, listener)` is deprecated, use `Airwallex.checkout(session, paymentMethod, cvc, saveCard, listener)` instead — build the `PaymentMethod` from your card/billing
    * `confirmPaymentIntent(session, paymentConsent, listener)` is deprecated, use `Airwallex.checkout(session, paymentConsent.paymentMethod!!, paymentConsentId = paymentConsent.id, paymentConsent = paymentConsent, listener)` instead
    * `confirmPaymentIntent(session, paymentConsentId, listener)` is deprecated. Prefer `Airwallex.checkout(session, paymentConsent.paymentMethod!!, paymentConsent = paymentConsent, listener)` and pass a `PaymentConsent` that contains at least `id` and `nextTriggeredBy`. The `paymentConsentId` parameter on `Airwallex.checkout(...)` is deprecated and retained only for backwards compatibility with legacy integrations — it should not be used in new code
    * `Airwallex.checkout()` is now public and is the single entry point for all card and consent-based payments; `Airwallex.startGooglePay()` remains the entry point for Google Pay

## Migrating from version 6.5.0
- `PaymentAppearance` has been moved from `AirwallexConfiguration` to `PaymentElementConfiguration` and renamed into `Appearance`
- `showsGooglePayAsPrimaryButton` has been moved to `googlePayButton.showsAsPrimaryButton` in `PaymentElementConfiguration.PaymentSheet`

## Migrating from versions < 6.5.0
- Changes to `AirwallexStarter`:
    * `presentCardPaymentFlow()` with `supportedCards` parameter is deprecated, use the new overload with `PaymentElementConfiguration.Card` instead
    * `presentEntirePaymentFlow()` with `layoutType` and `showsGooglePayAsPrimaryButton` parameters is deprecated, use the new overload with `PaymentElementConfiguration.PaymentSheet` instead

## Migrating from versions < 6.0.0
- Changes to `Airwallex`:
    * `ClientSecretProvider` has been removed as a parameter from `Airwallex.Companion#initialize()`
    * An overloaded function for `confirmPaymentIntent()` has been added to support completing payments using the `paymentConsent` parameter
    * The `activity` parameter type in the constructor has been changed from `Activity` to `ComponentActivity`
- Changes to `AirwallexStarter`:
    * `ClientSecretProvider` has been removed as a parameter from `AirwallexStarter.initialize()`
    * A new method `AirwallexStarter.presentCardPaymentFlow()` has been added
    * `AirwallexStarter.presentPaymentFlow()` has been deprecated and will be removed in a future release. Use `AirwallexStarter.presentEntirePaymentFlow()` instead
    * `ComponentActivity` is now required for `presentCardPaymentFlow()` and `presentEntirePaymentFlow()` methods. `presentShippingFlow()` supports both `Fragment` and `ComponentActivity` overloads

## Migrating from versions < 5.0.0
- Changes to `Airwallex`:
    * An `application` parameter has been added to `Airwallex.initialize()`
    * `handlePaymentData()` has been deprecated and will be removed in a future release
- Changes to `AirwallexStarter`:
    * An `application` parameter has been added to `AirwallexStarter.initialize()`
    * `handlePaymentData()` has been removed
- Dependency version updates:
    * `androidx.lifecycle:lifecycle-runtime-ktx`: `2.5.1` → `2.8.2`
    * `androidx.fragment:fragment-ktx`: `1.7.1` → `1.8.1`
    * `androidx.preference:preference-ktx`: `1.1.1` → `1.2.1`
