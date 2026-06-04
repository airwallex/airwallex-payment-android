//[components-core](../../../index.md)/[com.airwallex.android.core.log](../index.md)/[AnalyticsLogger](index.md)/[setSessionInformation](set-session-information.md)

# setSessionInformation

[androidJvm]\
fun [setSessionInformation](set-session-information.md)(transactionMode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), launchType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), expressCheckout: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), layout: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, showsGooglePayAsPrimaryButton: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null, legacyConsentFlow: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null)

Sets the current session information.

#### Parameters

androidJvm

| | |
|---|---|
| transactionMode | The current transaction mode. |
| paymentIntentId | The payment intent ID (optional). |
