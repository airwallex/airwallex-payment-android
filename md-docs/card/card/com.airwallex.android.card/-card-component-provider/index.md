//[card](../../../index.md)/[com.airwallex.android.card](../index.md)/[CardComponentProvider](index.md)

# CardComponentProvider

[androidJvm]\
class [CardComponentProvider](index.md) : [ActionComponentProvider](../../../../components-core/components-core/com.airwallex.android.core/-action-component-provider/index.md)&lt;[CardComponent](../-card-component/index.md)&gt;

## Constructors

| | |
|---|---|
| [CardComponentProvider](-card-component-provider.md) | [androidJvm]<br>constructor() |

## Functions

| Name | Summary |
|---|---|
| [canHandleAction](can-handle-action.md) | [androidJvm]<br>open override fun [canHandleAction](can-handle-action.md)(nextAction: [NextAction](../../../../components-core/components-core/com.airwallex.android.core.model/-next-action/index.md)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [canHandleSessionAndPaymentMethod](can-handle-session-and-payment-method.md) | [androidJvm]<br>open suspend override fun [canHandleSessionAndPaymentMethod](can-handle-session-and-payment-method.md)(session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), paymentMethodType: [AvailablePaymentMethodType](../../../../components-core/components-core/com.airwallex.android.core.model/-available-payment-method-type/index.md), activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [get](get.md) | [androidJvm]<br>open override fun [get](get.md)(): [CardComponent](../-card-component/index.md) |
| [getType](get-type.md) | [androidJvm]<br>open override fun [getType](get-type.md)(): [ActionComponentProviderType](../../../../components-core/components-core/com.airwallex.android.core/-action-component-provider-type/index.md) |
