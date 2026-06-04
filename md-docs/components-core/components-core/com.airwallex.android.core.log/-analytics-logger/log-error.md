//[components-core](../../../index.md)/[com.airwallex.android.core.log](../index.md)/[AnalyticsLogger](index.md)/[logError](log-error.md)

# logError

[androidJvm]\
fun [logError](log-error.md)(eventName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt;)

Logs a generic error event.

#### Parameters

androidJvm

| | |
|---|---|
| eventName | The name of the error event. |
| additionalInfo | Additional information about the error. |

[androidJvm]\
fun [logError](log-error.md)(eventName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), exception: [AirwallexException](../../com.airwallex.android.core.exception/-airwallex-exception/index.md), additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt;? = null)

Logs an error event with exception details.

#### Parameters

androidJvm

| | |
|---|---|
| eventName | The name of the error event. |
| exception | The exception that occurred. |
| additionalInfo | Additional information about the error. |
