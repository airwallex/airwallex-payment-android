//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[TrackerRequest](index.md)

# TrackerRequest

[androidJvm]\
data class [TrackerRequest](index.md) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../-object-builder/index.md)&lt;[TrackerRequest](index.md)&gt; |
| [TrackerCode](-tracker-code/index.md) | [androidJvm]<br>enum [TrackerCode](-tracker-code/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[TrackerRequest.TrackerCode](-tracker-code/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |
| [TrackerType](-tracker-type/index.md) | [androidJvm]<br>enum [TrackerType](-tracker-type/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[TrackerRequest.TrackerType](-tracker-type/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |

## Properties

| Name | Summary |
|---|---|
| [application](application.md) | [androidJvm]<br>val [application](application.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The application that add this log |
| [brand](brand.md) | [androidJvm]<br>val [brand](brand.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The brand of the card |
| [cardBin](card-bin.md) | [androidJvm]<br>val [cardBin](card-bin.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The card bin of the card |
| [code](code.md) | [androidJvm]<br>val [code](code.md): [TrackerRequest.TrackerCode](-tracker-code/index.md)? = null<br>The event type |
| [complete](complete.md) | [androidJvm]<br>val [complete](complete.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null<br>If the field is complete |
| [empty](empty.md) | [androidJvm]<br>val [empty](empty.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null<br>If the field is empty |
| [error](error.md) | [androidJvm]<br>val [error](error.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The error that occurring |
| [header](header.md) | [androidJvm]<br>val [header](header.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;? = null<br>The request header |
| [intentId](intent-id.md) | [androidJvm]<br>val [intentId](intent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The intent id you’re processing |
| [nextActionType](next-action-type.md) | [androidJvm]<br>val [nextActionType](next-action-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The next action type if have |
| [nextActionUrl](next-action-url.md) | [androidJvm]<br>val [nextActionUrl](next-action-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The next action url if have |
| [origin](origin.md) | [androidJvm]<br>val [origin](origin.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>A unique string to identify a merchant, like website origin url or merchant account id |
| [path](path.md) | [androidJvm]<br>val [path](path.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The request path |
| [req](req.md) | [androidJvm]<br>val [req](req.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;? = null<br>The request body |
| [res](res.md) | [androidJvm]<br>val [res](res.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;? = null<br>The request response |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [status](status.md) | [androidJvm]<br>val [status](status.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The status of the intent |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [TrackerRequest.TrackerType](-tracker-type/index.md)? = null<br>The element type |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
