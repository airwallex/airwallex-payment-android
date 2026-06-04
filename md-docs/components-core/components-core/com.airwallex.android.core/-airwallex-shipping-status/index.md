//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexShippingStatus](index.md)

# AirwallexShippingStatus

sealed class [AirwallexShippingStatus](index.md)

#### Inheritors

| |
|---|
| [Success](-success/index.md) |
| [Failure](-failure/index.md) |
| [Cancel](-cancel/index.md) |

## Types

| Name | Summary |
|---|---|
| [Cancel](-cancel/index.md) | [androidJvm]<br>object [Cancel](-cancel/index.md) : [AirwallexShippingStatus](index.md) |
| [Failure](-failure/index.md) | [androidJvm]<br>data class [Failure](-failure/index.md)(val exception: [AirwallexException](../../com.airwallex.android.core.exception/-airwallex-exception/index.md)) : [AirwallexShippingStatus](index.md) |
| [Success](-success/index.md) | [androidJvm]<br>data class [Success](-success/index.md)(val shipping: [Shipping](../../com.airwallex.android.core.model/-shipping/index.md)) : [AirwallexShippingStatus](index.md) |
