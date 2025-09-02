# Migration Guide

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
