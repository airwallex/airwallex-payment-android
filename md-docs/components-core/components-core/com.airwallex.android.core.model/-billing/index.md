//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[Billing](index.md)

# Billing

[androidJvm]\
data class [Billing](index.md) : [AirwallexModel](../-airwallex-model/index.md), [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Billing information.

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../-object-builder/index.md)&lt;[Billing](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [address](address.md) | [androidJvm]<br>val [address](address.md): [Address](../-address/index.md)? = null<br>The billing address as it appears on the credit card issuer’s records |
| [dateOfBirth](date-of-birth.md) | [androidJvm]<br>val [dateOfBirth](date-of-birth.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Date of birth of the customer in the format: YYYY-MM-DD |
| [email](email.md) | [androidJvm]<br>val [email](email.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Email address of the customer |
| [firstName](first-name.md) | [androidJvm]<br>val [firstName](first-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>First name of the customer |
| [lastName](last-name.md) | [androidJvm]<br>val [lastName](last-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Last name of the customer |
| [phone](phone.md) | [androidJvm]<br>val [phone](phone.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Phone number of the customer |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [validateForRequiredFields](../../com.airwallex.android.core.util/validate-for-required-fields.md) | [androidJvm]<br>fun [Billing](index.md)?.[validateForRequiredFields](../../com.airwallex.android.core.util/validate-for-required-fields.md)(requiredFields: [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../../com.airwallex.android.core/-required-billing-contact-field/index.md)&gt;): [InvalidParamsException](../../com.airwallex.android.core.exception/-invalid-params-exception/index.md)?<br>Verify a [Billing](index.md) payload satisfies every field in [requiredFields](../../com.airwallex.android.core.util/validate-for-required-fields.md). Returns `null` when valid, or an [InvalidParamsException](../../com.airwallex.android.core.exception/-invalid-params-exception/index.md) describing the first missing/ invalid field. Mirrors the iOS validation rules in `AWXDefaultProvider+Extensions.swift`. |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
