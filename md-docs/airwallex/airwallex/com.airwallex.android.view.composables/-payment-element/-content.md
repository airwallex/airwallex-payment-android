//[airwallex](../../../index.md)/[com.airwallex.android.view.composables](../index.md)/[PaymentElement](index.md)/[Content](-content.md)

# Content

[androidJvm]\

@[Composable](https://developer.android.com/reference/kotlin/androidx/compose/runtime/Composable.html)

fun [Content](-content.md)()

Renders the payment element composable using pre-fetched data from the ViewModel.

This composable does NOT fetch data - all data is already loaded during [create](-companion/create.md). It always uses the [PaymentFlowListener](../../com.airwallex.android.view/-payment-flow-listener/index.md) provided at creation.

Note: For Java developers, use [renderIn](render-in.md) instead of calling this directly.
