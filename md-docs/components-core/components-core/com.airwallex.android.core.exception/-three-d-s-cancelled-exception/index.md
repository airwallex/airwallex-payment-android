//[components-core](../../../index.md)/[com.airwallex.android.core.exception](../index.md)/[ThreeDSCancelledException](index.md)

# ThreeDSCancelledException

[androidJvm]\
class [ThreeDSCancelledException](index.md)(message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) = &quot;3DS has been cancelled!&quot;) : [AirwallexException](../-airwallex-exception/index.md)

Exception thrown when user cancels 3D Secure authentication

## Constructors

| | |
|---|---|
| [ThreeDSCancelledException](-three-d-s-cancelled-exception.md) | [androidJvm]<br>constructor(message: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) = &quot;3DS has been cancelled!&quot;) |

## Properties

| Name | Summary |
|---|---|
| [cause](index.md#-654012527%2FProperties%2F1424399983) | [androidJvm]<br>open val [cause](index.md#-654012527%2FProperties%2F1424399983): [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)? |
| [error](../-airwallex-exception/error.md) | [androidJvm]<br>val [error](../-airwallex-exception/error.md): [AirwallexError](../../com.airwallex.android.core.model/-airwallex-error/index.md)? |
| [message](index.md#1824300659%2FProperties%2F1424399983) | [androidJvm]<br>open val [message](index.md#1824300659%2FProperties%2F1424399983): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [statusCode](../-airwallex-exception/status-code.md) | [androidJvm]<br>val [statusCode](../-airwallex-exception/status-code.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |

## Functions

| Name | Summary |
|---|---|
| [toString](../-airwallex-exception/to-string.md) | [androidJvm]<br>open override fun [toString](../-airwallex-exception/to-string.md)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
