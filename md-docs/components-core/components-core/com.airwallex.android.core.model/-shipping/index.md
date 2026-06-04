//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[Shipping](index.md)

# Shipping

[androidJvm]\
data class [Shipping](index.md) : [AirwallexModel](../-airwallex-model/index.md), [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Shipping information

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../-object-builder/index.md)&lt;[Shipping](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [address](address.md) | [androidJvm]<br>val [address](address.md): [Address](../-address/index.md)? = null |
| [email](email.md) | [androidJvm]<br>val [email](email.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Email address of the customer |
| [firstName](first-name.md) | [androidJvm]<br>val [firstName](first-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>First name of the recipient |
| [lastName](last-name.md) | [androidJvm]<br>val [lastName](last-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Last name of the recipient |
| [phoneNumber](phone-number.md) | [androidJvm]<br>val [phoneNumber](phone-number.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Phone number of the recipient |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [shippingMethod](shipping-method.md) | [androidJvm]<br>val [shippingMethod](shipping-method.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Shipping method for the product |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
