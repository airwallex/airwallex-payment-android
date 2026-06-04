//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[AirwallexPaymentSession](../index.md)/[Builder](index.md)/[setRequireEmail](set-require-email.md)

# setRequireEmail

[androidJvm]\
fun [~~setRequireEmail~~](set-require-email.md)(requiresEmail: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [AirwallexPaymentSession.Builder](index.md)

---

### Deprecated

Use setRequiredBillingContactFields(...) and include RequiredBillingContactField.EMAIL to require email.

#### Replace with

```kotlin
setRequiredBillingContactFields(fields)
```
---
