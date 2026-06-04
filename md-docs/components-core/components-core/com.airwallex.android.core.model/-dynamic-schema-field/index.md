//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[DynamicSchemaField](index.md)

# DynamicSchemaField

[androidJvm]\
data class [DynamicSchemaField](index.md)(val name: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val displayName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val uiType: [DynamicSchemaFieldUIType](../-dynamic-schema-field-u-i-type/index.md)?, val type: [DynamicSchemaFieldType](../-dynamic-schema-field-type/index.md)?, val hidden: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), val candidates: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[DynamicSchemaFieldCandidate](../-dynamic-schema-field-candidate/index.md)&gt;?, val validations: [DynamicSchemaFieldValidation](../-dynamic-schema-field-validation/index.md)?) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [DynamicSchemaField](-dynamic-schema-field.md) | [androidJvm]<br>constructor(name: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), displayName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), uiType: [DynamicSchemaFieldUIType](../-dynamic-schema-field-u-i-type/index.md)?, type: [DynamicSchemaFieldType](../-dynamic-schema-field-type/index.md)?, hidden: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), candidates: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[DynamicSchemaFieldCandidate](../-dynamic-schema-field-candidate/index.md)&gt;?, validations: [DynamicSchemaFieldValidation](../-dynamic-schema-field-validation/index.md)?) |

## Properties

| Name | Summary |
|---|---|
| [candidates](candidates.md) | [androidJvm]<br>val [candidates](candidates.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[DynamicSchemaFieldCandidate](../-dynamic-schema-field-candidate/index.md)&gt;?<br>Validations of schema field |
| [displayName](display-name.md) | [androidJvm]<br>val [displayName](display-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Display name of schema field |
| [hidden](hidden.md) | [androidJvm]<br>val [hidden](hidden.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>If need to hide |
| [name](name.md) | [androidJvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Name of schema field |
| [type](type.md) | [androidJvm]<br>val [type](type.md): [DynamicSchemaFieldType](../-dynamic-schema-field-type/index.md)?<br>The type of schema field |
| [uiType](ui-type.md) | [androidJvm]<br>val [uiType](ui-type.md): [DynamicSchemaFieldUIType](../-dynamic-schema-field-u-i-type/index.md)?<br>UI type of schema field, include text, email, phone, list, logo_list |
| [validations](validations.md) | [androidJvm]<br>val [validations](validations.md): [DynamicSchemaFieldValidation](../-dynamic-schema-field-validation/index.md)?<br>Validations of schema field |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
