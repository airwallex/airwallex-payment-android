//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentMethod](../index.md)/[Card](index.md)

# Card

[androidJvm]\
data class [Card](index.md) : [AirwallexModel](../../-airwallex-model/index.md), [AirwallexRequestModel](../../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../../-object-builder/index.md)&lt;[PaymentMethod.Card](index.md)&gt; |
| [NumberType](-number-type/index.md) | [androidJvm]<br>enum [NumberType](-number-type/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentMethod.Card.NumberType](-number-type/index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [avsCheck](avs-check.md) | [androidJvm]<br>val [avsCheck](avs-check.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Whether address pass the check |
| [bin](bin.md) | [androidJvm]<br>val [bin](bin.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Bank identify number of this card |
| [brand](brand.md) | [androidJvm]<br>val [brand](brand.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Brand of the card |
| [cardType](card-type.md) | [androidJvm]<br>val [cardType](card-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Card type of the card |
| [country](country.md) | [androidJvm]<br>val [country](country.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Country of the card |
| [cvc](cvc.md) | [androidJvm]<br>val [cvc](cvc.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>CVC holder name |
| [cvcCheck](cvc-check.md) | [androidJvm]<br>val [cvcCheck](cvc-check.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Whether CVC pass the check |
| [expiryMonth](expiry-month.md) | [androidJvm]<br>val [expiryMonth](expiry-month.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Two digit number representing the card’s expiration month |
| [expiryYear](expiry-year.md) | [androidJvm]<br>val [expiryYear](expiry-year.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Four digit number representing the card’s expiration year |
| [fingerprint](fingerprint.md) | [androidJvm]<br>val [fingerprint](fingerprint.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Fingerprint of the card |
| [funding](funding.md) | [androidJvm]<br>val [funding](funding.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Funding of the card |
| [issuerCountryCode](issuer-country-code.md) | [androidJvm]<br>val [issuerCountryCode](issuer-country-code.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Country code of the card issuer |
| [last4](last4.md) | [androidJvm]<br>val [last4](last4.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Last four digits of the card number |
| [name](name.md) | [androidJvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Card holder name |
| [number](number.md) | [androidJvm]<br>val [number](number.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Number of the card |
| [numberType](number-type.md) | [androidJvm]<br>val [numberType](number-type.md): [PaymentMethod.Card.NumberType](-number-type/index.md)? = null<br>Type of the number |
| [sdkType](../../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
