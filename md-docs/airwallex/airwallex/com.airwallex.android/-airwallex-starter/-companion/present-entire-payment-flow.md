//[airwallex](../../../../index.md)/[com.airwallex.android](../../index.md)/[AirwallexStarter](../index.md)/[Companion](index.md)/[presentEntirePaymentFlow](present-entire-payment-flow.md)

# presentEntirePaymentFlow

[androidJvm]\
fun [presentEntirePaymentFlow](present-entire-payment-flow.md)(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), configuration: [PaymentElementConfiguration.PaymentSheet](../../../com.airwallex.android.view.composables/-payment-element-configuration/-payment-sheet/index.md), paymentResultListener: [Airwallex.PaymentResultListener](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md))

Launch the payment flow to allow the user to complete the entire payment flow

#### Parameters

androidJvm

| | |
|---|---|
| activity | the launch activity on which the payment UI is presented |
| session | a [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md) used to present the payment flow |
| configuration | Configuration for the card payment element |
| paymentResultListener | The callback of present entire payment flow |

[androidJvm]\
fun [~~presentEntirePaymentFlow~~](present-entire-payment-flow.md)(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), layoutType: [PaymentMethodsLayoutType](../../../../../components-core/components-core/com.airwallex.android.core/-payment-methods-layout-type/index.md) = PaymentMethodsLayoutType.TAB, showsGooglePayAsPrimaryButton: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true, paymentResultListener: [Airwallex.PaymentResultListener](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md))

---

### Deprecated

Use presentEntirePaymentFlow() with PaymentElementConfiguration.PaymentSheet instead

#### Replace with

```kotlin
import com.airwallex.android.view.composables.PaymentElementConfiguration

```
```kotlin
presentEntirePaymentFlow(activity, session, PaymentElementConfiguration.PaymentSheet(layout = layoutType, googlePayButton = PaymentElementConfiguration.GooglePayButton(showsAsPrimaryButton = showsGooglePayAsPrimaryButton)), paymentResultListener)
```
---

Launch the payment flow to allow the user to complete the entire payment flow

#### Parameters

androidJvm

| | |
|---|---|
| activity | the launch activity on which the payment UI is presented |
| session | a [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md) used to present the payment flow |
| paymentResultListener | The callback of present entire payment flow |
| layoutType | PaymentMethodsLayoutType for payment methods list UI. Two types are supported: Tab and Accordion. |
| showsGooglePayAsPrimaryButton | If true, shows Google Pay as primary button. If false, shows in payment method list. |
