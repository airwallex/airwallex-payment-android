//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentConsentCreateRequest](index.md)

# PaymentConsentCreateRequest

[androidJvm]\
data class [PaymentConsentCreateRequest](index.md) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Params for create a [PaymentConsent](../-payment-consent/index.md)

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../-object-builder/index.md)&lt;[PaymentConsentCreateRequest](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [customerId](customer-id.md) | [androidJvm]<br>val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>ID from Airwallex of the customer for whom the consent is created |
| [merchantTriggerReason](merchant-trigger-reason.md) | [androidJvm]<br>val [merchantTriggerReason](merchant-trigger-reason.md): [PaymentConsent.MerchantTriggerReason](../-payment-consent/-merchant-trigger-reason/index.md)?<br>Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled, installments. Default: unscheduled |
| [metadata](metadata.md) | [androidJvm]<br>val [metadata](metadata.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt;? = null<br>A set of key-value pairs that can be attached to this PaymentConsent |
| [nextTriggeredBy](next-triggered-by.md) | [androidJvm]<br>val [nextTriggeredBy](next-triggered-by.md): [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md)? = null<br>The party to trigger subsequent payments. Can be one of merchant, customer. If type of payment_method is card, both merchant and customer is supported. Otherwise, only merchant is supported |
| [paymentMethodRequest](payment-method-request.md) | [androidJvm]<br>val [paymentMethodRequest](payment-method-request.md): [PaymentMethodRequest](../-payment-method-request/index.md)? = null<br>PaymentMethod for subsequent payments. Can be provided later by updating the PaymentConsent |
| [requestId](request-id.md) | [androidJvm]<br>val [requestId](request-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique request ID specified by the merchant |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
