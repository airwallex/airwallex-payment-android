//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentConsentOptions](index.md)

# PaymentConsentOptions

[androidJvm]\
data class [PaymentConsentOptions](index.md)(val nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md), val merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../-payment-consent/-merchant-trigger-reason/index.md)? = null, val termsOfUse: [PaymentConsentOptions.TermsOfUse](-terms-of-use/index.md)? = null) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

Data class for payment_consent options in PaymentIntentConfirmRequest as per Airwallex API

## Constructors

| | |
|---|---|
| [PaymentConsentOptions](-payment-consent-options.md) | [androidJvm]<br>constructor(nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md), merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../-payment-consent/-merchant-trigger-reason/index.md)? = null, termsOfUse: [PaymentConsentOptions.TermsOfUse](-terms-of-use/index.md)? = null) |

## Types

| Name | Summary |
|---|---|
| [PaymentAmountType](-payment-amount-type/index.md) | [androidJvm]<br>enum [PaymentAmountType](-payment-amount-type/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentConsentOptions.PaymentAmountType](-payment-amount-type/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>The agreed type of amounts for subsequent payment |
| [PaymentSchedule](-payment-schedule/index.md) | [androidJvm]<br>data class [PaymentSchedule](-payment-schedule/index.md)(val period: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, val periodUnit: [PaymentConsentOptions.PeriodUnit](-period-unit/index.md)? = null) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |
| [PeriodUnit](-period-unit/index.md) | [androidJvm]<br>enum [PeriodUnit](-period-unit/index.md) : [Enum](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-enum/index.html)&lt;[PaymentConsentOptions.PeriodUnit](-period-unit/index.md)&gt; , [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)<br>Specifies billing frequency |
| [TermsOfUse](-terms-of-use/index.md) | [androidJvm]<br>data class [TermsOfUse](-terms-of-use/index.md)(val paymentAmountType: [PaymentConsentOptions.PaymentAmountType](-payment-amount-type/index.md), val fixedPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val maxPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val minPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val firstPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val paymentCurrency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val billingCycleChargeDay: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, val startDate: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val endDate: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val totalBillingCycles: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, val paymentSchedule: [PaymentConsentOptions.PaymentSchedule](-payment-schedule/index.md)? = null) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html) |

## Properties

| Name | Summary |
|---|---|
| [merchantTriggerReason](merchant-trigger-reason.md) | [androidJvm]<br>val [merchantTriggerReason](merchant-trigger-reason.md): [PaymentConsent.MerchantTriggerReason](../-payment-consent/-merchant-trigger-reason/index.md)? = null<br>Only applicable when next_triggered_by is merchant. One of scheduled, unscheduled, installments (optional) |
| [nextTriggeredBy](next-triggered-by.md) | [androidJvm]<br>val [nextTriggeredBy](next-triggered-by.md): [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md)<br>The party to trigger subsequent payments. One of merchant, customer (required) |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [termsOfUse](terms-of-use.md) | [androidJvm]<br>val [termsOfUse](terms-of-use.md): [PaymentConsentOptions.TermsOfUse](-terms-of-use/index.md)? = null<br>Terms of use for this PaymentConsent (optional) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
