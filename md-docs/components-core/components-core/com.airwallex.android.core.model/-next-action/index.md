//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[NextAction](index.md)

# NextAction

[androidJvm]\
data class [NextAction](index.md)(val stage: [NextAction.NextActionStage](-next-action-stage/index.md)? = null, val type: [NextAction.NextActionType](-next-action-type/index.md)?, val data: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;?, val dcc: [NextAction.DccData](-dcc-data/index.md)?, val url: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val fallbackUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val method: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val packageName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [NextAction](-next-action.md) | [androidJvm]<br>constructor(stage: [NextAction.NextActionStage](-next-action-stage/index.md)? = null, type: [NextAction.NextActionType](-next-action-type/index.md)?, data: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;?, dcc: [NextAction.DccData](-dcc-data/index.md)?, url: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, fallbackUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, method: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, packageName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |

## Types

| Name | Summary |
|---|---|
| [DccData](-dcc-data/index.md) | [androidJvm]<br>data class [DccData](-dcc-data/index.md) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>The status of a [PaymentIntent](../-payment-intent/index.md) |
| [NextActionStage](-next-action-stage/index.md) | [androidJvm]<br>enum [NextActionStage](-next-action-stage/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[NextAction.NextActionStage](-next-action-stage/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |
| [NextActionType](-next-action-type/index.md) | [androidJvm]<br>enum [NextActionType](-next-action-type/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[NextAction.NextActionType](-next-action-type/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>The status of a [PaymentIntent](../-payment-intent/index.md) |

## Properties

| Name | Summary |
|---|---|
| [data](data.md) | [androidJvm]<br>val [data](data.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;?<br>The additional data that can be used to complete this action |
| [dcc](dcc.md) | [androidJvm]<br>val [dcc](dcc.md): [NextAction.DccData](-dcc-data/index.md)?<br>The dcc data that can be used to complete this action |
| [fallbackUrl](fallback-url.md) | [androidJvm]<br>val [fallbackUrl](fallback-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [method](method.md) | [androidJvm]<br>val [method](method.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [packageName](package-name.md) | [androidJvm]<br>val [packageName](package-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [stage](stage.md) | [androidJvm]<br>val [stage](stage.md): [NextAction.NextActionStage](-next-action-stage/index.md)? = null |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [NextAction.NextActionType](-next-action-type/index.md)?<br>Type of next action, can be one of render_qr_code, call_sdk, redirect, display |
| [url](url.md) | [androidJvm]<br>val [url](url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
