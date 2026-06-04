//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[AirwallexRecurringWithIntentSession](../index.md)/[Builder](index.md)/[setRequireBillingInformation](set-require-billing-information.md)

# setRequireBillingInformation

[androidJvm]\
fun [~~setRequireBillingInformation~~](set-require-billing-information.md)(requiresBillingInformation: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [AirwallexRecurringWithIntentSession.Builder](index.md)

---

### Deprecated

Use setRequiredBillingContactFields(...) and include RequiredBillingContactField.ADDRESS &amp; PHONE to require billing info.

#### Replace with

```kotlin
setRequiredBillingContactFields(fields)
```
---
