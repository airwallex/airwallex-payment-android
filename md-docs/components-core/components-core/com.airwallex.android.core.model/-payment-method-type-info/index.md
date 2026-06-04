//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentMethodTypeInfo](index.md)

# PaymentMethodTypeInfo

[androidJvm]\
data class [PaymentMethodTypeInfo](index.md) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Properties

| Name | Summary |
|---|---|
| [displayName](display-name.md) | [androidJvm]<br>val [displayName](display-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Display name of the payment method |
| [fieldSchemas](field-schemas.md) | [androidJvm]<br>val [fieldSchemas](field-schemas.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[DynamicSchema](../-dynamic-schema/index.md)&gt;? = null<br>The detail required schema fields |
| [hasSchema](has-schema.md) | [androidJvm]<br>val [hasSchema](has-schema.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null<br>Check if the payment method have schema fields |
| [logos](logos.md) | [androidJvm]<br>val [logos](logos.md): [LogoResources](../../com.airwallex.android.core/-logo-resources/index.md)? = null<br>Logos of the payment method, include png & svg |
| [name](name.md) | [androidJvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Name of the payment method |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
