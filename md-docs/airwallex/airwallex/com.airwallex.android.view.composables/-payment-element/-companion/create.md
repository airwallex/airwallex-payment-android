//[airwallex](../../../../index.md)/[com.airwallex.android.view.composables](../../index.md)/[PaymentElement](../index.md)/[Companion](index.md)/[create](create.md)

# create

[androidJvm]\
suspend fun [create](create.md)(session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), airwallex: [Airwallex](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md), configuration: [PaymentElementConfiguration](../../-payment-element-configuration/index.md), paymentFlowListener: [PaymentFlowListener](../../../com.airwallex.android.view/-payment-flow-listener/index.md)): [Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[PaymentElement](../index.md)&gt;

Creates a [PaymentElement](../index.md) by fetching required data.

This method obtains a [PaymentFlowViewModel](../../../com.airwallex.android.view/-payment-flow-view-model/index.md) scoped to the Activity, checks if data is already loaded (for configuration changes), and fetches if necessary.

This is the public API that always uses EMBEDDED launch type. For SDK internal usage with custom launch types, use the internal overload.

#### Return

Result containing the PaymentElement or an error if fetching failed

#### Parameters

androidJvm

| | |
|---|---|
| session | The Airwallex session containing payment information |
| airwallex | The Airwallex instance for payment operations |
| configuration | Configuration for the payment element |
| paymentFlowListener | Listener for payment operation callbacks |

[androidJvm]\
suspend fun [create](create.md)(session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), airwallex: [Airwallex](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md), configuration: [PaymentElementConfiguration](../../-payment-element-configuration/index.md), onLoadingStateChanged: ([Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)? = null, onPaymentResult: ([AirwallexPaymentStatus](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-payment-status/index.md)) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html), onError: ([Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)? = null): [Result](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-result/index.html)&lt;[PaymentElement](../index.md)&gt;

Creates a [PaymentElement](../index.md) using callback-based approach.

This is a convenience method that wraps [PaymentFlowListener](../../../com.airwallex.android.view/-payment-flow-listener/index.md) with lambda callbacks.

#### Return

Result containing the PaymentElement or an error if fetching failed

#### Parameters

androidJvm

| | |
|---|---|
| session | The Airwallex session containing payment information |
| airwallex | The Airwallex instance for payment operations |
| configuration | Configuration for the payment element |
| onLoadingStateChanged | Optional callback when loading state changes |
| onPaymentResult | Callback when payment completes (success, failure, or cancel) |
| onError | Optional callback when errors occur during initialization or payment |

[androidJvm]\

@[JvmStatic](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/-jvm-static/index.html)

fun [create](create.md)(session: [AirwallexSession](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md), airwallex: [Airwallex](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/index.md), configuration: [PaymentElementConfiguration](../../-payment-element-configuration/index.md), paymentFlowListener: [PaymentFlowListener](../../../com.airwallex.android.view/-payment-flow-listener/index.md), callback: [PaymentElementCallback](../../-payment-element-callback/index.md))

Java-friendly: Creates a PaymentElement without automatic rendering.

Use this from Java code when you want to control rendering yourself. Call [PaymentElement.renderIn](../render-in.md) on the element to render it when ready.

Example usage from Java:

```java
PaymentElement.create(
    session,
    airwallex,
    configuration,
    listener,
    new PaymentElementCallback() {
        @Override
        public void onSuccess(PaymentElement element) {
            // Render when ready
            element.renderIn(composeView);
        }

        @Override
        public void onFailure(Throwable error) {
            // Handle error
        }
    }
);
```

#### Parameters

androidJvm

| | |
|---|---|
| session | The Airwallex session containing payment information |
| airwallex | The Airwallex instance for payment operations |
| configuration | Configuration for the payment element |
| paymentFlowListener | Listener for payment operation callbacks |
| callback | Callback to receive the result |
