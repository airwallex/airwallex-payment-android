//[airwallex](../../../../index.md)/[com.airwallex.android](../../index.md)/[AirwallexStarter](../index.md)/[Companion](index.md)/[presentShippingFlow](present-shipping-flow.md)

# presentShippingFlow

[androidJvm]\
fun [presentShippingFlow](present-shipping-flow.md)(fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html), shipping: [Shipping](../../../../../components-core/components-core/com.airwallex.android.core.model/-shipping/index.md)?, shippingResultListener: [Airwallex.ShippingResultListener](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-shipping-result-listener/index.md))

Launch the shipping flow to allow the user to fill the shipping information

#### Parameters

androidJvm

| | |
|---|---|
| fragment | fragment {@link Fragment} |
| shipping | a [Shipping](../../../../../components-core/components-core/com.airwallex.android.core.model/-shipping/index.md) used to present the shipping flow, it's optional |
| shippingResultListener | The callback of present the shipping flow |

[androidJvm]\
fun [presentShippingFlow](present-shipping-flow.md)(activity: [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/activity/ComponentActivity.html), shipping: [Shipping](../../../../../components-core/components-core/com.airwallex.android.core.model/-shipping/index.md)?, shippingResultListener: [Airwallex.ShippingResultListener](../../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-shipping-result-listener/index.md))

Launch the shipping flow to allow the user to fill the shipping information

#### Parameters

androidJvm

| | |
|---|---|
| activity | the launch activity on which the shipping UI is presented |
| shipping | a [Shipping](../../../../../components-core/components-core/com.airwallex.android.core.model/-shipping/index.md) used to present the shipping flow, it's optional |
| shippingResultListener | The callback of present the shipping flow |
