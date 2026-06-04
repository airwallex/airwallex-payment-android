//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentConsentOptions](../index.md)/[TermsOfUse](index.md)

# TermsOfUse

[androidJvm]\
data class [TermsOfUse](index.md)(val paymentAmountType: [PaymentConsentOptions.PaymentAmountType](../-payment-amount-type/index.md), val fixedPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val maxPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val minPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val firstPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val paymentCurrency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val billingCycleChargeDay: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, val startDate: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val endDate: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val totalBillingCycles: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, val paymentSchedule: [PaymentConsentOptions.PaymentSchedule](../-payment-schedule/index.md)? = null) : [AirwallexRequestModel](../../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [TermsOfUse](-terms-of-use.md) | [androidJvm]<br>constructor(paymentAmountType: [PaymentConsentOptions.PaymentAmountType](../-payment-amount-type/index.md), fixedPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, maxPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, minPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, firstPaymentAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, paymentCurrency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, billingCycleChargeDay: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, startDate: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, endDate: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, totalBillingCycles: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, paymentSchedule: [PaymentConsentOptions.PaymentSchedule](../-payment-schedule/index.md)? = null) |

## Properties

| Name | Summary |
|---|---|
| [billingCycleChargeDay](billing-cycle-charge-day.md) | [androidJvm]<br>val [billingCycleChargeDay](billing-cycle-charge-day.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null<br>The granularity per billing cycle. Required when payment_schedule.period_unit is WEEK, MONTH, or YEAR |
| [endDate](end-date.md) | [androidJvm]<br>val [endDate](end-date.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>End date to expect payment request (ISO8601 format) |
| [firstPaymentAmount](first-payment-amount.md) | [androidJvm]<br>val [firstPaymentAmount](first-payment-amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null<br>The first payment amount. Optional if payment agreement type is VARIABLE |
| [fixedPaymentAmount](fixed-payment-amount.md) | [androidJvm]<br>val [fixedPaymentAmount](fixed-payment-amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null<br>The fixed payment amount that can be charged for a single payment. Required if payment_amount_type is FIXED |
| [maxPaymentAmount](max-payment-amount.md) | [androidJvm]<br>val [maxPaymentAmount](max-payment-amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null<br>The maximum payment amount that can be charged for a single payment. Optional if payment_amount_type is VARIABLE |
| [minPaymentAmount](min-payment-amount.md) | [androidJvm]<br>val [minPaymentAmount](min-payment-amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null<br>The minimum payment amount that can be charged for a single payment. Optional if payment_amount_type is VARIABLE |
| [paymentAmountType](payment-amount-type.md) | [androidJvm]<br>val [paymentAmountType](payment-amount-type.md): [PaymentConsentOptions.PaymentAmountType](../-payment-amount-type/index.md)<br>The agreed type of amounts for subsequent payment (required) |
| [paymentCurrency](payment-currency.md) | [androidJvm]<br>val [paymentCurrency](payment-currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The currency of this payment |
| [paymentSchedule](payment-schedule.md) | [androidJvm]<br>val [paymentSchedule](payment-schedule.md): [PaymentConsentOptions.PaymentSchedule](../-payment-schedule/index.md)? = null<br>Payment schedule details (optional) |
| [sdkType](../../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [startDate](start-date.md) | [androidJvm]<br>val [startDate](start-date.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Start date to expect payment request (ISO8601 format) |
| [totalBillingCycles](total-billing-cycles.md) | [androidJvm]<br>val [totalBillingCycles](total-billing-cycles.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null<br>Total number of billing cycles |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
