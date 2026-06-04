//[components-core](../../../index.md)/[com.airwallex.android.core.exception](../index.md)/[AirwallexComponentDependencyException](index.md)

# AirwallexComponentDependencyException

[androidJvm]\
class [AirwallexComponentDependencyException](index.md)(dependency: [Dependency](../../com.airwallex.android.core.model/-dependency/index.md), traceId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val statusCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) = 0, e: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) : [AirwallexException](../-airwallex-exception/index.md)

An exception that is thrown when a required dependency for a component within the Airwallex SDK is missing.

## Constructors

| | |
|---|---|
| [AirwallexComponentDependencyException](-airwallex-component-dependency-exception.md) | [androidJvm]<br>constructor(dependency: [Dependency](../../com.airwallex.android.core.model/-dependency/index.md), traceId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, statusCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) = 0, e: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [cause](../-three-d-s-cancelled-exception/index.md#-654012527%2FProperties%2F1424399983) | [androidJvm]<br>open val [cause](../-three-d-s-cancelled-exception/index.md#-654012527%2FProperties%2F1424399983): [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? |
| [error](../-airwallex-exception/error.md) | [androidJvm]<br>val [error](../-airwallex-exception/error.md): [AirwallexError](../../com.airwallex.android.core.model/-airwallex-error/index.md)? |
| [message](../-three-d-s-cancelled-exception/index.md#1824300659%2FProperties%2F1424399983) | [androidJvm]<br>open val [message](../-three-d-s-cancelled-exception/index.md#1824300659%2FProperties%2F1424399983): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [statusCode](../-airwallex-exception/status-code.md) | [androidJvm]<br>val [statusCode](../-airwallex-exception/status-code.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [toString](../-airwallex-exception/to-string.md) | [androidJvm]<br>open override fun [toString](../-airwallex-exception/to-string.md)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
