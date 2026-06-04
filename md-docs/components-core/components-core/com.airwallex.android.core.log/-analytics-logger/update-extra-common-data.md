//[components-core](../../../index.md)/[com.airwallex.android.core.log](../index.md)/[AnalyticsLogger](index.md)/[updateExtraCommonData](update-extra-common-data.md)

# updateExtraCommonData

[androidJvm]\
fun [updateExtraCommonData](update-extra-common-data.md)(data: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt;)

Updates extra common data in the analytics tracker. This allows cross-platform SDKs (e.g., Flutter, React Native) to set custom fields.

#### Parameters

androidJvm

| | |
|---|---|
| data | A map of field keys to values to be merged into the extra common data. |
