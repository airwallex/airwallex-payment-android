//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexPaymentStatus](index.md)

# AirwallexPaymentStatus

sealed class [AirwallexPaymentStatus](index.md)

#### Inheritors

| |
|---|
| [Success](-success/index.md) |
| [InProgress](-in-progress/index.md) |
| [Failure](-failure/index.md) |
| [Cancel](-cancel/index.md) |

## Types

| Name | Summary |
|---|---|
| [Cancel](-cancel/index.md) | [androidJvm]<br>object [Cancel](-cancel/index.md) : [AirwallexPaymentStatus](index.md) |
| [Failure](-failure/index.md) | [androidJvm]<br>data class [Failure](-failure/index.md)(val exception: [AirwallexException](../../com.airwallex.android.core.exception/-airwallex-exception/index.md)) : [AirwallexPaymentStatus](index.md) |
| [InProgress](-in-progress/index.md) | [androidJvm]<br>data class [InProgress](-in-progress/index.md)(val paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [AirwallexPaymentStatus](index.md) |
| [Success](-success/index.md) | [androidJvm]<br>data class [Success](-success/index.md)(val paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val consentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt;? = null) : [AirwallexPaymentStatus](index.md) |
