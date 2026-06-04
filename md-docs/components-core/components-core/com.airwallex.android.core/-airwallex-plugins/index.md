//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexPlugins](index.md)

# AirwallexPlugins

[androidJvm]\
object [AirwallexPlugins](index.md)

Provide some internal plugins

## Properties

| Name | Summary |
|---|---|
| [AIRWALLEX_USER_AGENT](-a-i-r-w-a-l-l-e-x_-u-s-e-r_-a-g-e-n-t.md) | [androidJvm]<br>const val [AIRWALLEX_USER_AGENT](-a-i-r-w-a-l-l-e-x_-u-s-e-r_-a-g-e-n-t.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [environment](environment.md) | [androidJvm]<br>val [environment](environment.md): [Environment](../-environment/index.md)<br>Environment in the Airwallex |
| [redirectMode](redirect-mode.md) | [androidJvm]<br>val [redirectMode](redirect-mode.md): [RedirectMode](../-redirect-mode/index.md) |

## Functions

| Name | Summary |
|---|---|
| [getProvider](get-provider.md) | [androidJvm]<br>fun [getProvider](get-provider.md)(type: [ActionComponentProviderType](../-action-component-provider-type/index.md)): [ActionComponentProvider](../-action-component-provider/index.md)&lt;out [ActionComponent](../-action-component/index.md)&gt;?<br>fun [getProvider](get-provider.md)(paymentMethodType: [AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md)): [ActionComponentProvider](../-action-component-provider/index.md)&lt;out [ActionComponent](../-action-component/index.md)&gt;?<br>fun [getProvider](get-provider.md)(nextAction: [NextAction](../../com.airwallex.android.core.model/-next-action/index.md)?): [ActionComponentProvider](../-action-component-provider/index.md)&lt;out [ActionComponent](../-action-component/index.md)&gt;? |
| [initialize](initialize.md) | [androidJvm]<br>fun [initialize](initialize.md)(configuration: [AirwallexConfiguration](../-airwallex-configuration/index.md)) |
