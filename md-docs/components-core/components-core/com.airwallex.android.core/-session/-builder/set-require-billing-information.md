//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[Session](../index.md)/[Builder](index.md)/[setRequireBillingInformation](set-require-billing-information.md)

# setRequireBillingInformation

[androidJvm]\
fun [~~setRequireBillingInformation~~](set-require-billing-information.md)(requireBillingInformation: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [Session.Builder](index.md)

---

### Deprecated

Use setRequiredBillingContactFields(...) and include RequiredBillingContactField.ADDRESS &amp; PHONE to require billing info.

#### Replace with

```kotlin
setRequiredBillingContactFields(fields)
```
---
