//[airwallex](../../../../index.md)/[com.airwallex.android](../../index.md)/[AirwallexStarter](../index.md)/[Companion](index.md)/[presentCardPaymentFlow](present-card-payment-flow.md)

# presentCardPaymentFlow

[androidJvm]\
fun [presentCardPaymentFlow](present-card-payment-flow.md)(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), configuration: [PaymentElementConfiguration.Card](../../../com.airwallex.android.view.composables/-payment-element-configuration/-card/index.md), paymentResultListener: [Airwallex.PaymentResultListener](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md))

Launch the card payment flow to allow the user to complete the entire payment flow

#### Parameters

androidJvm

| | |
|---|---|
| activity | the launch activity on which the payment UI is presented |
| session | a [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md) used to present the payment flow |
| configuration | Configuration for the card payment element |
| paymentResultListener | The callback of present entire payment flow |

[androidJvm]\
fun [~~presentCardPaymentFlow~~](present-card-payment-flow.md)(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), supportedCards: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexSupportedCard](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)&gt; = enumValues&lt;AirwallexSupportedCard&gt;().toList(), paymentResultListener: [Airwallex.PaymentResultListener](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md))

---

### Deprecated

Use presentCardPaymentFlow with PaymentElementConfiguration.Card parameter instead

#### Replace with

```kotlin
import com.airwallex.android.view.composables.PaymentElementConfiguration

```
```kotlin
presentCardPaymentFlow(activity, session, PaymentElementConfiguration.Card(supportedCardBrands = supportedCards), paymentResultListener)
```
---

Launch the card payment flow to allow the user to complete the entire payment flow

#### Parameters

androidJvm

| | |
|---|---|
| activity | the launch activity on which the payment UI is presented |
| session | a [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md) used to present the payment flow |
| supportedCards | deprecated parameter, use presentCardPaymentFlow with configuration instead |
| paymentResultListener | The callback of present entire payment flow |
