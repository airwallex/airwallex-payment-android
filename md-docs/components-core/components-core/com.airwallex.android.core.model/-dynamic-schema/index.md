//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[DynamicSchema](index.md)

# DynamicSchema

[androidJvm]\
data class [DynamicSchema](index.md)(val transactionMode: [TransactionMode](../-transaction-mode/index.md)? = null, val fields: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[DynamicSchemaField](../-dynamic-schema-field/index.md)&gt;? = null) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [DynamicSchema](-dynamic-schema.md) | [androidJvm]<br>constructor(transactionMode: [TransactionMode](../-transaction-mode/index.md)? = null, fields: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[DynamicSchemaField](../-dynamic-schema-field/index.md)&gt;? = null) |

## Properties

| Name | Summary |
|---|---|
| [fields](fields.md) | [androidJvm]<br>val [fields](fields.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[DynamicSchemaField](../-dynamic-schema-field/index.md)&gt;? = null<br>Required payment method schema field |
| [transactionMode](transaction-mode.md) | [androidJvm]<br>val [transactionMode](transaction-mode.md): [TransactionMode](../-transaction-mode/index.md)? = null<br>The supported transaction mode. One of oneoff, recurring. |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
