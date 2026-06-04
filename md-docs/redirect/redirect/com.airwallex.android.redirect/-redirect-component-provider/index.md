//[redirect](../../../index.md)/[com.airwallex.android.redirect](../index.md)/[RedirectComponentProvider](index.md)

# RedirectComponentProvider

[androidJvm]\
class [RedirectComponentProvider](index.md) : [ActionComponentProvider](../../../../components-core/components-core/com.airwallex.android.core/-action-component-provider/index.md)&lt;[RedirectComponent](../-redirect-component/index.md)&gt;

## Constructors

| | |
|---|---|
| [RedirectComponentProvider](-redirect-component-provider.md) | [androidJvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [canHandleAction](can-handle-action.md) | [androidJvm]<br>open override fun [canHandleAction](can-handle-action.md)(nextAction: [NextAction](../../../../components-core/components-core/com.airwallex.android.core.model/-next-action/index.md)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [canHandleSessionAndPaymentMethod](index.md#1918133822%2FFunctions%2F319035559) | [androidJvm]<br>open suspend fun [canHandleSessionAndPaymentMethod](index.md#1918133822%2FFunctions%2F319035559)(session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), paymentMethodType: [AvailablePaymentMethodType](../../../../components-core/components-core/com.airwallex.android.core.model/-available-payment-method-type/index.md), activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [get](get.md) | [androidJvm]<br>open override fun [get](get.md)(): [RedirectComponent](../-redirect-component/index.md) |
| [getType](get-type.md) | [androidJvm]<br>open override fun [getType](get-type.md)(): [ActionComponentProviderType](../../../../components-core/components-core/com.airwallex.android.core/-action-component-provider-type/index.md) |
