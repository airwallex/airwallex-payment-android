//[components-core](../../../index.md)/[com.airwallex.android.core.log](../index.md)/[Crasher](index.md)

# Crasher

[androidJvm]\
object [Crasher](index.md) : [Thread.UncaughtExceptionHandler](https://developer.android.com/reference/kotlin/java/lang/Thread.UncaughtExceptionHandler.html)

Uncaught exception handler that logs crashes originating inside the Airwallex SDK and forwards the exception to the previously installed default handler.

Crashes whose root cause originated outside the SDK (e.g. in the host app) are passed through untouched so we don't pollute analytics with merchant-side bugs.

## Functions

| Name | Summary |
|---|---|
| [initialize](initialize.md) | [androidJvm]<br>fun [initialize](initialize.md)() |
| [uncaughtException](uncaught-exception.md) | [androidJvm]<br>open override fun [uncaughtException](uncaught-exception.md)(thread: [Thread](https://developer.android.com/reference/kotlin/java/lang/Thread.html), throwable: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)) |
