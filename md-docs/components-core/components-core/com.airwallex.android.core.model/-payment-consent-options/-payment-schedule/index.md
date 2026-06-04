//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentConsentOptions](../index.md)/[PaymentSchedule](index.md)

# PaymentSchedule

[androidJvm]\
data class [PaymentSchedule](index.md)(val period: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, val periodUnit: [PaymentConsentOptions.PeriodUnit](../-period-unit/index.md)? = null) : [AirwallexRequestModel](../../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [PaymentSchedule](-payment-schedule.md) | [androidJvm]<br>constructor(period: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null, periodUnit: [PaymentConsentOptions.PeriodUnit](../-period-unit/index.md)? = null) |

## Properties

| Name | Summary |
|---|---|
| [period](period.md) | [androidJvm]<br>val [period](period.md): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)? = null<br>The number of period units between billing cycles. For example, period=1 and period_unit=MONTH means monthly billing. Required when merchant_trigger_reason = scheduled |
| [periodUnit](period-unit.md) | [androidJvm]<br>val [periodUnit](period-unit.md): [PaymentConsentOptions.PeriodUnit](../-period-unit/index.md)? = null<br>Specifies billing frequency. Required when merchant_trigger_reason = scheduled |
| [sdkType](../../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
