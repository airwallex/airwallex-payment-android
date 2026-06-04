//[security-3ds](../../../../index.md)/[com.airwallex.android.threedsecurity](../../index.md)/[ThreeDSecurityActivityLaunch](../index.md)/[Result](index.md)

# Result

[androidJvm]\
data class [Result](index.md) : [AirwallexActivityLaunch.Result](../../../../../ui-core/ui-core/com.airwallex.android.ui/-airwallex-activity-launch/-result/index.md)

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) : Parceler&lt;[ThreeDSecurityActivityLaunch.Result](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [exception](exception.md) | [androidJvm]<br>val [exception](exception.md): [AirwallexException](../../../../../components-core/components-core/com.airwallex.android.core.exception/-airwallex-exception/index.md)? = null |
| [paymentIntentId](payment-intent-id.md) | [androidJvm]<br>val [paymentIntentId](payment-intent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null |

## Functions

| Name | Summary |
|---|---|
| [describeContents](index.md#-1578325224%2FFunctions%2F-1832291072) | [androidJvm]<br>abstract fun [describeContents](index.md#-1578325224%2FFunctions%2F-1832291072)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toBundle](to-bundle.md) | [androidJvm]<br>open override fun [toBundle](to-bundle.md)(): [Bundle](https://developer.android.com/reference/kotlin/android/os/Bundle.html) |
| [write](-companion/write.md) | [androidJvm]<br>open override fun [ThreeDSecurityActivityLaunch.Result](index.md).[write](-companion/write.md)(parcel: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), flags: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
| [writeToParcel](index.md#-1754457655%2FFunctions%2F-1832291072) | [androidJvm]<br>abstract fun [writeToParcel](index.md#-1754457655%2FFunctions%2F-1832291072)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
