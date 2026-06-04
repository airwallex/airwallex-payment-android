//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[Airwallex](../index.md)/[Companion](index.md)

# Companion

[androidJvm]\
object [Companion](index.md)

## Properties

| Name | Summary |
|---|---|
| [AIRWALLEX_CHECKOUT_SCHEMA](-a-i-r-w-a-l-l-e-x_-c-h-e-c-k-o-u-t_-s-c-h-e-m-a.md) | [androidJvm]<br>const val [AIRWALLEX_CHECKOUT_SCHEMA](-a-i-r-w-a-l-l-e-x_-c-h-e-c-k-o-u-t_-s-c-h-e-m-a.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [initialize](initialize.md) | [androidJvm]<br>fun [initialize](initialize.md)(application: [Application](https://developer.android.com/reference/kotlin/android/app/Application.html), configuration: [AirwallexConfiguration](../../-airwallex-configuration/index.md))<br>Initialize some global configurations, better to be called on Application |
| [initializeComponents](initialize-components.md) | [androidJvm]<br>fun [initializeComponents](initialize-components.md)(application: [Application](https://developer.android.com/reference/kotlin/android/app/Application.html), supportComponentProviders: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[ActionComponentProvider](../../-action-component-provider/index.md)&lt;out [ActionComponent](../../-action-component/index.md)&gt;&gt;)<br>Initialize Airwallex Components, if you have invoked [initialize](initialize.md) before, no need to call this method |
