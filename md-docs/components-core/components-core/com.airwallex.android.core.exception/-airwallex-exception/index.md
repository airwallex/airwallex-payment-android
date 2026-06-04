//[components-core](../../../index.md)/[com.airwallex.android.core.exception](../index.md)/[AirwallexException](index.md)

# AirwallexException

abstract class [AirwallexException](index.md)@[JvmOverloads](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/-jvm-overloads/index.html)constructor(val error: [AirwallexError](../../com.airwallex.android.core.model/-airwallex-error/index.md)?, traceId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val statusCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = error?.message, e: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) : [Exception](https://developer.android.com/reference/kotlin/java/lang/Exception.html)

Parent class for exceptions encountered when using the SDK.

#### Inheritors

| |
|---|
| [APIConnectionException](../-a-p-i-connection-exception/index.md) |
| [APIException](../-a-p-i-exception/index.md) |
| [AirwallexCheckoutException](../-airwallex-checkout-exception/index.md) |
| [AirwallexComponentDependencyException](../-airwallex-component-dependency-exception/index.md) |
| [AuthenticationException](../-authentication-exception/index.md) |
| [InvalidParamsException](../-invalid-params-exception/index.md) |
| [InvalidRequestException](../-invalid-request-exception/index.md) |
| [PermissionException](../-permission-exception/index.md) |
| [ThreeDSCancelledException](../-three-d-s-cancelled-exception/index.md) |

## Constructors

| | |
|---|---|
| [AirwallexException](-airwallex-exception.md) | [androidJvm]<br>@[JvmOverloads](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/-jvm-overloads/index.html)<br>constructor(error: [AirwallexError](../../com.airwallex.android.core.model/-airwallex-error/index.md)?, traceId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, statusCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = error?.message, e: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? = null) |

## Properties

| Name | Summary |
|---|---|
| [cause](../-three-d-s-cancelled-exception/index.md#-654012527%2FProperties%2F1424399983) | [androidJvm]<br>open val [cause](../-three-d-s-cancelled-exception/index.md#-654012527%2FProperties%2F1424399983): [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? |
| [error](error.md) | [androidJvm]<br>val [error](error.md): [AirwallexError](../../com.airwallex.android.core.model/-airwallex-error/index.md)? |
| [message](../-three-d-s-cancelled-exception/index.md#1824300659%2FProperties%2F1424399983) | [androidJvm]<br>open val [message](../-three-d-s-cancelled-exception/index.md#1824300659%2FProperties%2F1424399983): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [statusCode](status-code.md) | [androidJvm]<br>val [statusCode](status-code.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [toString](to-string.md) | [androidJvm]<br>open override fun [toString](to-string.md)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
