//[components-core](../../../index.md)/[com.airwallex.android.core.log](../index.md)/[AirwallexLogger](index.md)

# AirwallexLogger

[androidJvm]\
object [AirwallexLogger](index.md)

Formatted log for Airwallex

## Types

| Name | Summary |
|---|---|
| [Level](-level/index.md) | [androidJvm]<br>enum [Level](-level/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[AirwallexLogger.Level](-level/index.md)&gt; |
| [LogWorker](-log-worker/index.md) | [androidJvm]<br>interface [LogWorker](-log-worker/index.md) |

## Functions

| Name | Summary |
|---|---|
| [debug](debug.md) | [androidJvm]<br>fun [debug](debug.md)(message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, throwable: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) |
| [error](error.md) | [androidJvm]<br>fun [error](error.md)(message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, throwable: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) |
| [info](info.md) | [androidJvm]<br>fun [info](info.md)(message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, throwable: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null)<br>fun [info](info.md)(message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, throwable: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null, sensitiveMessage: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |
| [initialize](initialize.md) | [androidJvm]<br>fun [initialize](initialize.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), loggingEnabled: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false, saveLogToLocal: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false, logWorker: [AirwallexLogger.LogWorker](-log-worker/index.md) = DEFAULT_LOG_WORKER) |
| [verbose](verbose.md) | [androidJvm]<br>fun [verbose](verbose.md)(message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, throwable: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) |
| [warn](warn.md) | [androidJvm]<br>fun [warn](warn.md)(message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, throwable: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) |
