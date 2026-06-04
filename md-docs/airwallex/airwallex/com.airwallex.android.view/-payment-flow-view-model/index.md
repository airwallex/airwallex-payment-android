//[airwallex](../../../index.md)/[com.airwallex.android.view](../index.md)/[PaymentFlowViewModel](index.md)

# PaymentFlowViewModel

[androidJvm]\
class [PaymentFlowViewModel](index.md)(airwallex: [Airwallex](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md), session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md)) : [ViewModel](https://developer.android.com/reference/kotlin/androidx/lifecycle/ViewModel.html)

ViewModel for managing payment operations data. Handles fetching and storing payment methods and consents.

## Constructors

| | |
|---|---|
| [PaymentFlowViewModel](-payment-flow-view-model.md) | [androidJvm]<br>constructor(airwallex: [Airwallex](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md), session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md)) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |
| [DeleteConsentResult](-delete-consent-result/index.md) | [androidJvm]<br>sealed class [DeleteConsentResult](-delete-consent-result/index.md)<br>Result of deleting a payment consent |
| [Factory](-factory/index.md) | [androidJvm]<br>class [Factory](-factory/index.md)(airwallex: [Airwallex](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md), session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md)) : [ViewModelProvider.Factory](https://developer.android.com/reference/kotlin/androidx/lifecycle/ViewModelProvider.Factory.html) |
| [PaymentFlowType](-payment-flow-type/index.md) | [androidJvm]<br>enum [PaymentFlowType](-payment-flow-type/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentFlowViewModel.PaymentFlowType](-payment-flow-type/index.md)&gt; |
| [PaymentResultEvent](-payment-result-event/index.md) | [androidJvm]<br>data class [PaymentResultEvent](-payment-result-event/index.md)(val flowType: [PaymentFlowViewModel.PaymentFlowType](-payment-flow-type/index.md), val status: [AirwallexPaymentStatus](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-payment-status/index.md))<br>Event that wraps payment status with the flow type that triggered it |

## Properties

| Name | Summary |
|---|---|
| [availablePaymentConsents](available-payment-consents.md) | [androidJvm]<br>val [availablePaymentConsents](available-payment-consents.md): StateFlow&lt;[List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[PaymentConsent](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md)&gt;&gt; |
| [availablePaymentMethods](available-payment-methods.md) | [androidJvm]<br>val [availablePaymentMethods](available-payment-methods.md): StateFlow&lt;[List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AvailablePaymentMethodType](../../../../components-core/components-core/com.airwallex.android.core.model/-available-payment-method-type/index.md)&gt;&gt; |
| [deleteConsentResult](delete-consent-result.md) | [androidJvm]<br>val [deleteConsentResult](delete-consent-result.md): SharedFlow&lt;[PaymentFlowViewModel.DeleteConsentResult](-delete-consent-result/index.md)&gt; |
| [paymentResult](payment-result.md) | [androidJvm]<br>val [paymentResult](payment-result.md): Flow&lt;[PaymentFlowViewModel.PaymentResultEvent](-payment-result-event/index.md)&gt; |

## Functions

| Name | Summary |
|---|---|
| [addCloseable](../-schema-payment-view-model/index.md#383812252%2FFunctions%2F1201863744) | [androidJvm]<br>open fun [addCloseable](../-schema-payment-view-model/index.md#383812252%2FFunctions%2F1201863744)(closeable: [AutoCloseable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-auto-closeable/index.html))<br>fun [addCloseable](../-schema-payment-view-model/index.md#1722490497%2FFunctions%2F1201863744)(key: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), closeable: [AutoCloseable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-auto-closeable/index.html)) |
| [checkoutWithCvc](checkout-with-cvc.md) | [androidJvm]<br>fun [checkoutWithCvc](checkout-with-cvc.md)(paymentConsent: [PaymentConsent](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md), cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): Job |
| [checkoutWithGooglePay](checkout-with-google-pay.md) | [androidJvm]<br>fun [checkoutWithGooglePay](checkout-with-google-pay.md)(): Job |
| [checkoutWithNewCard](checkout-with-new-card.md) | [androidJvm]<br>fun [checkoutWithNewCard](checkout-with-new-card.md)(card: [PaymentMethod.Card](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-method/-card/index.md), saveCard: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), billing: [Billing](../../../../components-core/components-core/com.airwallex.android.core.model/-billing/index.md)?): Job |
| [checkoutWithoutCvc](checkout-without-cvc.md) | [androidJvm]<br>fun [checkoutWithoutCvc](checkout-without-cvc.md)(paymentConsent: [PaymentConsent](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md)) |
| [deletePaymentConsent](delete-payment-consent.md) | [androidJvm]<br>fun [deletePaymentConsent](delete-payment-consent.md)(paymentConsent: [PaymentConsent](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md)): Job |
| [fetchAvailablePaymentMethodsAndConsents](fetch-available-payment-methods-and-consents.md) | [androidJvm]<br>suspend fun [fetchAvailablePaymentMethodsAndConsents](fetch-available-payment-methods-and-consents.md)(): [Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[Pair](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-pair/index.html)&lt;[List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AvailablePaymentMethodType](../../../../components-core/components-core/com.airwallex.android.core.model/-available-payment-method-type/index.md)&gt;, [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[PaymentConsent](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md)&gt;&gt;&gt;<br>Fetches available payment methods and consents. Should only be called once from the parent component. |
| [getCloseable](../-schema-payment-view-model/index.md#1102255800%2FFunctions%2F1201863744) | [androidJvm]<br>fun &lt;[T](../-schema-payment-view-model/index.md#1102255800%2FFunctions%2F1201863744) : [AutoCloseable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-auto-closeable/index.html)&gt; [getCloseable](../-schema-payment-view-model/index.md#1102255800%2FFunctions%2F1201863744)(key: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [T](../-schema-payment-view-model/index.md#1102255800%2FFunctions%2F1201863744)? |
| [observeResults](observe-results.md) | [androidJvm]<br>fun [observeResults](observe-results.md)(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), listener: [PaymentFlowListener](../-payment-flow-listener/index.md))<br>Subscribe [listener](observe-results.md) to [paymentResult](payment-result.md), cancelling any previous subscription. The channel is single-receiver, so a stale collector from a dismissed UI would otherwise swallow results meant for the current one. |
| [trackScreenViewed](track-screen-viewed.md) | [androidJvm]<br>fun [trackScreenViewed](track-screen-viewed.md)(eventName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), params: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; = emptyMap()) |
| [updateActivity](update-activity.md) | [androidJvm]<br>fun [updateActivity](update-activity.md)(newActivity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html)) |
| [updateSession](update-session.md) | [androidJvm]<br>fun [updateSession](update-session.md)(newSession: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md))<br>Swap the session this VM operates on. The VM is Activity-scoped, so without this a second checkout on the same Activity would reuse the factory-injected session and confirm against an already-completed PaymentIntent. Resets per-session caches so PaymentSheet refetches methods/consents. |
