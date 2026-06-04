//[airwallex](../../../index.md)/[com.airwallex.android.view](../index.md)/[PaymentFlowViewModel](index.md)/[observeResults](observe-results.md)

# observeResults

[androidJvm]\
fun [observeResults](observe-results.md)(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), listener: [PaymentFlowListener](../-payment-flow-listener/index.md))

Subscribe [listener](observe-results.md) to [paymentResult](payment-result.md), cancelling any previous subscription. The channel is single-receiver, so a stale collector from a dismissed UI would otherwise swallow results meant for the current one.
