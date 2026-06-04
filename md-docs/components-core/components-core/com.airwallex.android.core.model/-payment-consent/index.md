//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentConsent](index.md)

# PaymentConsent

[androidJvm]\
data class [PaymentConsent](index.md)(val id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val requestId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, var paymentMethod: [PaymentMethod](../-payment-method/index.md)? = null, val initialPaymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val nextTriggeredBy: [PaymentConsent.NextTriggeredBy](-next-triggered-by/index.md)? = null, val merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](-merchant-trigger-reason/index.md)? = null, val requiresCvc: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false, val metadata: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;? = null, val status: [PaymentConsent.PaymentConsentStatus](-payment-consent-status/index.md)? = null, val createdAt: [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)? = null, val updatedAt: [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)? = null, val nextAction: [NextAction](../-next-action/index.md)? = null, val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) : [AirwallexModel](../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [PaymentConsent](-payment-consent.md) | [androidJvm]<br>constructor(id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, requestId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, paymentMethod: [PaymentMethod](../-payment-method/index.md)? = null, initialPaymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, nextTriggeredBy: [PaymentConsent.NextTriggeredBy](-next-triggered-by/index.md)? = null, merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](-merchant-trigger-reason/index.md)? = null, requiresCvc: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false, metadata: [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;? = null, status: [PaymentConsent.PaymentConsentStatus](-payment-consent-status/index.md)? = null, createdAt: [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)? = null, updatedAt: [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)? = null, nextAction: [NextAction](../-next-action/index.md)? = null, clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) |

## Types

| Name | Summary |
|---|---|
| [MerchantTriggerReason](-merchant-trigger-reason/index.md) | [androidJvm]<br>enum [MerchantTriggerReason](-merchant-trigger-reason/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentConsent.MerchantTriggerReason](-merchant-trigger-reason/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>Only applicable when next_triggered_by is merchant |
| [NextTriggeredBy](-next-triggered-by/index.md) | [androidJvm]<br>enum [NextTriggeredBy](-next-triggered-by/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentConsent.NextTriggeredBy](-next-triggered-by/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>The party to trigger subsequent payments. |
| [PaymentConsentStatus](-payment-consent-status/index.md) | [androidJvm]<br>enum [PaymentConsentStatus](-payment-consent-status/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentConsent.PaymentConsentStatus](-payment-consent-status/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>Only applicable when next_triggered_by is merchant |

## Properties

| Name | Summary |
|---|---|
| [clientSecret](client-secret.md) | [androidJvm]<br>val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Client secret for browser or app |
| [createdAt](created-at.md) | [androidJvm]<br>val [createdAt](created-at.md): [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)? = null<br>Time at which this PaymentConsent was created |
| [customerId](customer-id.md) | [androidJvm]<br>val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Airwallex ID of the customer for whom the PaymentConsent is created |
| [id](id.md) | [androidJvm]<br>val [id](id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique identifier of this PaymentConsent |
| [initialPaymentIntentId](initial-payment-intent-id.md) | [androidJvm]<br>val [initialPaymentIntentId](initial-payment-intent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>ID of the initial PaymentIntent confirmed with this PaymentConsent |
| [merchantTriggerReason](merchant-trigger-reason.md) | [androidJvm]<br>val [merchantTriggerReason](merchant-trigger-reason.md): [PaymentConsent.MerchantTriggerReason](-merchant-trigger-reason/index.md)? = null<br>Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled, installments |
| [metadata](metadata.md) | [androidJvm]<br>val [metadata](metadata.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)?&gt;? = null<br>A set of key-value pairs that can be attached to this PaymentConsent |
| [nextAction](next-action.md) | [androidJvm]<br>val [nextAction](next-action.md): [NextAction](../-next-action/index.md)? = null<br>Next action for merchant |
| [nextTriggeredBy](next-triggered-by.md) | [androidJvm]<br>val [nextTriggeredBy](next-triggered-by.md): [PaymentConsent.NextTriggeredBy](-next-triggered-by/index.md)? = null<br>The party to trigger subsequent payments. One of merchant, customer |
| [paymentMethod](payment-method.md) | [androidJvm]<br>var [paymentMethod](payment-method.md): [PaymentMethod](../-payment-method/index.md)?<br>PaymentMethod information attached for subsequent payments |
| [requestId](request-id.md) | [androidJvm]<br>val [requestId](request-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique request ID specified by the merchant |
| [requiresCvc](requires-cvc.md) | [androidJvm]<br>val [requiresCvc](requires-cvc.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false<br>Only applicable when next_triggered_by is customer. If false, the customer must provide cvc for subsequent payments with this PaymentConsent. |
| [status](status.md) | [androidJvm]<br>val [status](status.md): [PaymentConsent.PaymentConsentStatus](-payment-consent-status/index.md)? = null<br>Status of this PaymentConsent. One of PENDING_VERIFICATION, VERIFIED, DISABLED |
| [updatedAt](updated-at.md) | [androidJvm]<br>val [updatedAt](updated-at.md): [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)? = null<br>Time at which this PaymentConsent was last updated |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
