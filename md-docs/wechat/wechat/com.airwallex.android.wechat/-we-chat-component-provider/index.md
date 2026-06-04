//[wechat](../../../index.md)/[com.airwallex.android.wechat](../index.md)/[WeChatComponentProvider](index.md)

# WeChatComponentProvider

[androidJvm]\
class [WeChatComponentProvider](index.md) : [ActionComponentProvider](../../../../components-core/components-core/com.airwallex.android.core/-action-component-provider/index.md)&lt;[WeChatComponent](../-we-chat-component/index.md)&gt;

## Constructors

| | |
|---|---|
| [WeChatComponentProvider](-we-chat-component-provider.md) | [androidJvm]<br>constructor() |

## Properties

| Name | Summary |
|---|---|
| [weChatComponent](we-chat-component.md) | [androidJvm]<br>val [weChatComponent](we-chat-component.md): [WeChatComponent](../-we-chat-component/index.md) |

## Functions

| Name | Summary |
|---|---|
| [canHandleAction](can-handle-action.md) | [androidJvm]<br>open override fun [canHandleAction](can-handle-action.md)(nextAction: [NextAction](../../../../components-core/components-core/com.airwallex.android.core.model/-next-action/index.md)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [canHandleSessionAndPaymentMethod](index.md#1918133822%2FFunctions%2F-147910223) | [androidJvm]<br>open suspend fun [canHandleSessionAndPaymentMethod](index.md#1918133822%2FFunctions%2F-147910223)(session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), paymentMethodType: [AvailablePaymentMethodType](../../../../components-core/components-core/com.airwallex.android.core.model/-available-payment-method-type/index.md), activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html)): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [get](get.md) | [androidJvm]<br>open override fun [get](get.md)(): [WeChatComponent](../-we-chat-component/index.md) |
| [getType](get-type.md) | [androidJvm]<br>open override fun [getType](get-type.md)(): [ActionComponentProviderType](../../../../components-core/components-core/com.airwallex.android.core/-action-component-provider-type/index.md) |
