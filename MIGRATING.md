# Migration Guide

## Migrating from versions < 6.0.0

- Changes to `Airwallex`:
    * `ClientSecretProvider` has been removed as a parameter on `Airwallex.Companion#initialize`
    * Add an overloaded function for `confirmPaymentIntent()` to support completing the payment using the `paymentConsent` parameter.
    * Replace the parameter `activity` type in the constructor from `Activity` to `ComponentActivity`.
- Changes to `AirwallexStarter`:
    * `ClientSecretProvider` has been removed as a parameter on `AirwallexStarter.initialize()`
    * Add a method `AirwallexStarter.presentCardPaymentFlow()`
    * `AirwallexStarter.presentPaymentFlow()` has been deprecated and will be removed in a future
      release. Use the `AirwallexStarter.presentEntirePaymentFlow()` method instead.
    * `ComponentActivity` is now required for all payment methods, replace all `Activity`
      to `ComponentActivity` in all public payment methods.

## Migrating from versions < 5.0.0

- Changes to `Airwallex`:
    * Add a parameter`application` to `Airwallex.initialize()`.
    * `handlePaymentData()` has been deprecated and will be removed in a future release.

- Changes to `AirwallexStarter`:
    * Add a parameter`application` to `AirwallexStarter.initialize()`.
    * `handlePaymentData()` has been removed.

- The following dependencies version are changed

   * `androidx.lifecycle:lifecycle-runtime-ktx:2.5.1` -> `androidx.lifecycle:lifecycle-runtime-ktx:2.8.2`
   * `androidx.fragment:fragment-ktx:1.7.1` -> `androidx.fragment:fragment-ktx:1.8.1`
   * `androidx.preference:preference-ktx:1.1.1` -> `androidx.preference:preference-ktx:1.2.1`
