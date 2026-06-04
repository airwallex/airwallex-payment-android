//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[AvailablePaymentMethodType](index.md)

# AvailablePaymentMethodType

[androidJvm]\
data class [AvailablePaymentMethodType](index.md)(val name: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val displayName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val transactionMode: [TransactionMode](../-transaction-mode/index.md)? = null, val flows: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexPaymentRequestFlow](../-airwallex-payment-request-flow/index.md)&gt;? = null, val transactionCurrencies: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, val countryCodes: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, val active: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null, val resources: [AvailablePaymentMethodTypeResource](../-available-payment-method-type-resource/index.md)? = null, val cardSchemes: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CardScheme](../-card-scheme/index.md)&gt;? = null) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [AvailablePaymentMethodType](-available-payment-method-type.md) | [androidJvm]<br>constructor(name: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), displayName: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, transactionMode: [TransactionMode](../-transaction-mode/index.md)? = null, flows: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexPaymentRequestFlow](../-airwallex-payment-request-flow/index.md)&gt;? = null, transactionCurrencies: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, countryCodes: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null, active: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null, resources: [AvailablePaymentMethodTypeResource](../-available-payment-method-type-resource/index.md)? = null, cardSchemes: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CardScheme](../-card-scheme/index.md)&gt;? = null) |

## Properties

| Name | Summary |
|---|---|
| [active](active.md) | [androidJvm]<br>val [active](active.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)? = null<br>Indicate whether the payment method type is active |
| [cardSchemes](card-schemes.md) | [androidJvm]<br>val [cardSchemes](card-schemes.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CardScheme](../-card-scheme/index.md)&gt;? = null<br>Supported card schemes. |
| [countryCodes](country-codes.md) | [androidJvm]<br>val [countryCodes](country-codes.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null<br>The supported country codes for the payment method type and the transaction mode |
| [displayName](display-name.md) | [androidJvm]<br>val [displayName](display-name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The display name of payment method type. |
| [flows](flows.md) | [androidJvm]<br>val [flows](flows.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[AirwallexPaymentRequestFlow](../-airwallex-payment-request-flow/index.md)&gt;? = null<br>The supported flows for the payment method type and the transaction mode. A flow can be one of webqr, mweb, inapp |
| [name](name.md) | [androidJvm]<br>val [name](name.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>The name of payment method type. |
| [resources](resources.md) | [androidJvm]<br>val [resources](resources.md): [AvailablePaymentMethodTypeResource](../-available-payment-method-type-resource/index.md)? = null<br>The resources of payment method |
| [transactionCurrencies](transaction-currencies.md) | [androidJvm]<br>val [transactionCurrencies](transaction-currencies.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null<br>The supported transaction currencies for the payment method type and the transaction mode |
| [transactionMode](transaction-mode.md) | [androidJvm]<br>val [transactionMode](transaction-mode.md): [TransactionMode](../-transaction-mode/index.md)? = null<br>Indicate in which mode you trigger transactions with the payment method type. One of oneoff, recurring. |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
