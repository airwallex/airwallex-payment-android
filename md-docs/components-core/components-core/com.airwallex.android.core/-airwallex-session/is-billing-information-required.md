//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexSession](index.md)/[isBillingInformationRequired](is-billing-information-required.md)

# isBillingInformationRequired

[androidJvm]\
abstract val [~~isBillingInformationRequired~~](is-billing-information-required.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)

---

### Deprecated

Use requiredBillingContactFields to explicitly declare which billing contact fields (ADDRESS, PHONE, etc.) the SDK should collect.

#### Replace with

```kotlin
requiredBillingContactFields
```
---

Whether or not billing information is required for card payments.
