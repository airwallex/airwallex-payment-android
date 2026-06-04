//[airwallex](../../../index.md)/[com.airwallex.android.view](../index.md)/[AirwallexAddPaymentDialog](index.md)/[AirwallexAddPaymentDialog](-airwallex-add-payment-dialog.md)

# AirwallexAddPaymentDialog

[androidJvm]\

@[JvmOverloads](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/-jvm-overloads/index.html)

constructor(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), configuration: [PaymentElementConfiguration.Card](../../com.airwallex.android.view.composables/-payment-element-configuration/-card/index.md) = PaymentElementConfiguration.Card(), paymentResultListener: [Airwallex.PaymentResultListener](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md), dialogHeight: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null)

[androidJvm]\
constructor(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), session: [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), supportedCardBrands: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexSupportedCard](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-supported-card/index.md)&gt;, paymentResultListener: [Airwallex.PaymentResultListener](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md), dialogHeight: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null)

---

### Deprecated

Use constructor with PaymentElementConfiguration.Card instead

#### Replace with

```kotlin
import com.airwallex.android.view.composables.PaymentElementConfiguration

```
```kotlin
AirwallexAddPaymentDialog(activity, session, PaymentElementConfiguration.Card(supportedCardBrands = supportedCardBrands), paymentResultListener, dialogHeight)
```
---

Deprecated constructor with supportedCardBrands parameter
