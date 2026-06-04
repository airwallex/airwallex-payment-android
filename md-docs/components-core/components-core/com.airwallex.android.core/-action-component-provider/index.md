//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[ActionComponentProvider](index.md)

# ActionComponentProvider

[androidJvm]\
interface [ActionComponentProvider](index.md)&lt;[Component](index.md) : [ActionComponent](../-action-component/index.md)?&gt;

## Functions

| Name | Summary |
|---|---|
| [canHandleAction](can-handle-action.md) | [androidJvm]<br>abstract fun [canHandleAction](can-handle-action.md)(nextAction: [NextAction](../../com.airwallex.android.core.model/-next-action/index.md)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [canHandleSessionAndPaymentMethod](can-handle-session-and-payment-method.md) | [androidJvm]<br>open suspend fun [canHandleSessionAndPaymentMethod](can-handle-session-and-payment-method.md)(session: [AirwallexSession](../-airwallex-session/index.md), paymentMethodType: [AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md), activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [get](get.md) | [androidJvm]<br>abstract fun [get](get.md)(): [Component](index.md) |
| [getType](get-type.md) | [androidJvm]<br>abstract fun [getType](get-type.md)(): [ActionComponentProviderType](../-action-component-provider-type/index.md) |
