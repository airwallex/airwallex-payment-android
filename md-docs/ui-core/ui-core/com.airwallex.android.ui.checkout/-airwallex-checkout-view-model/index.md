//[ui-core](../../../index.md)/[com.airwallex.android.ui.checkout](../index.md)/[AirwallexCheckoutViewModel](index.md)

# AirwallexCheckoutViewModel

[androidJvm]\
open class [AirwallexCheckoutViewModel](index.md)(application: [Application](https://developer.android.com/reference/kotlin/android/app/Application.html), val airwallex: [Airwallex](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md), session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md)) : [AndroidViewModel](https://developer.android.com/reference/kotlin/androidx/lifecycle/AndroidViewModel.html)

## Constructors

| | |
|---|---|
| [AirwallexCheckoutViewModel](-airwallex-checkout-view-model.md) | [androidJvm]<br>constructor(application: [Application](https://developer.android.com/reference/kotlin/android/app/Application.html), airwallex: [Airwallex](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md), session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md)) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [airwallex](airwallex.md) | [androidJvm]<br>val [airwallex](airwallex.md): [Airwallex](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md) |
| [transactionMode](transaction-mode.md) | [androidJvm]<br>val [transactionMode](transaction-mode.md): [TransactionMode](../../../../components-core/components-core/com.airwallex.android.core.model/-transaction-mode/index.md) |

## Functions

| Name | Summary |
|---|---|
| [addCloseable](index.md#383812252%2FFunctions%2F-1628350927) | [androidJvm]<br>open fun [addCloseable](index.md#383812252%2FFunctions%2F-1628350927)(closeable: [AutoCloseable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-auto-closeable/index.html))<br>fun [addCloseable](index.md#1722490497%2FFunctions%2F-1628350927)(key: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), closeable: [AutoCloseable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-auto-closeable/index.html)) |
| [checkout](checkout.md) | [androidJvm]<br>suspend fun [checkout](checkout.md)(paymentMethod: [PaymentMethod](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-method/index.md), additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, flow: [AirwallexPaymentRequestFlow](../../../../components-core/components-core/com.airwallex.android.core.model/-airwallex-payment-request-flow/index.md)? = null): [AirwallexPaymentStatus](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-payment-status/index.md)<br>fun [checkout](checkout.md)(paymentMethod: [PaymentMethod](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-method/index.md), paymentConsent: [PaymentConsent](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md)?, cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, additionalInfo: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, flow: [AirwallexPaymentRequestFlow](../../../../components-core/components-core/com.airwallex.android.core.model/-airwallex-payment-request-flow/index.md)? = null): [LiveData](https://developer.android.com/reference/kotlin/androidx/lifecycle/LiveData.html)&lt;[AirwallexPaymentStatus](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-payment-status/index.md)&gt; |
| [checkoutGooglePay](checkout-google-pay.md) | [androidJvm]<br>suspend fun [checkoutGooglePay](checkout-google-pay.md)(): [AirwallexPaymentStatus](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-payment-status/index.md) |
| [getApplication](index.md#1696759283%2FFunctions%2F-1628350927) | [androidJvm]<br>open fun &lt;[T](index.md#1696759283%2FFunctions%2F-1628350927) : [Application](https://developer.android.com/reference/kotlin/android/app/Application.html)&gt; [getApplication](index.md#1696759283%2FFunctions%2F-1628350927)(): [T](index.md#1696759283%2FFunctions%2F-1628350927) |
| [getCloseable](index.md#1102255800%2FFunctions%2F-1628350927) | [androidJvm]<br>fun &lt;[T](index.md#1102255800%2FFunctions%2F-1628350927) : [AutoCloseable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-auto-closeable/index.html)&gt; [getCloseable](index.md#1102255800%2FFunctions%2F-1628350927)(key: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [T](index.md#1102255800%2FFunctions%2F-1628350927)? |
| [retrieveBanks](retrieve-banks.md) | [androidJvm]<br>suspend fun [retrieveBanks](retrieve-banks.md)(paymentMethodTypeName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[BankResponse](../../../../components-core/components-core/com.airwallex.android.core.model/-bank-response/index.md)&gt; |
| [retrievePaymentMethodTypeInfo](retrieve-payment-method-type-info.md) | [androidJvm]<br>suspend fun [retrievePaymentMethodTypeInfo](retrieve-payment-method-type-info.md)(paymentMethodTypeName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[PaymentMethodTypeInfo](../../../../components-core/components-core/com.airwallex.android.core.model/-payment-method-type-info/index.md)&gt; |
| [trackPaymentCancelled](track-payment-cancelled.md) | [androidJvm]<br>fun [trackPaymentCancelled](track-payment-cancelled.md)() |
| [trackScreenViewed](track-screen-viewed.md) | [androidJvm]<br>fun [trackScreenViewed](track-screen-viewed.md)(eventName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), params: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; = emptyMap()) |
| [updateActivity](update-activity.md) | [androidJvm]<br>fun [updateActivity](update-activity.md)(newActivity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html))<br>Update the Airwallex instance when the activity is recreated. This should be called in the activity's onCreate or onStart. |
