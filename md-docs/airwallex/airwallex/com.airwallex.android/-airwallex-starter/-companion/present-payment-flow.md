//[airwallex](../../../../index.md)/[com.airwallex.android](../../index.md)/[AirwallexStarter](../index.md)/[Companion](index.md)/[presentPaymentFlow](present-payment-flow.md)

# presentPaymentFlow

[androidJvm]\
fun [~~presentPaymentFlow~~](present-payment-flow.md)(fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html), session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), layoutType: [PaymentMethodsLayoutType](../../../../../components-core/components-core/com.airwallex.android.core/-payment-methods-layout-type/index.md) = PaymentMethodsLayoutType.TAB, paymentResultListener: [Airwallex.PaymentResultListener](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md))

---

### Deprecated

Use presentEntirePaymentFlow() instead

---

Launch the payment flow to allow the user to complete the entire payment flow

#### Parameters

androidJvm

| | |
|---|---|
| fragment | fragment {@link Fragment} |
| session | a [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md) used to present the payment flow |
| paymentResultListener | The callback of present entire payment flow |
| layoutType | PaymentMethodsLayoutType for payment methods list UI. Two types are supported: Tab and Accordion. |

[androidJvm]\
fun [~~presentPaymentFlow~~](present-payment-flow.md)(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), layoutType: [PaymentMethodsLayoutType](../../../../../components-core/components-core/com.airwallex.android.core/-payment-methods-layout-type/index.md) = PaymentMethodsLayoutType.TAB, paymentResultListener: [Airwallex.PaymentResultListener](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md))

---

### Deprecated

Use presentEntirePaymentFlow() instead

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
