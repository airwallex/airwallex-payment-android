//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[Options](../index.md)/[RetrieveAvailablePaymentMethodsOptions](index.md)

# RetrieveAvailablePaymentMethodsOptions

[androidJvm]\
data class [RetrieveAvailablePaymentMethodsOptions](index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), pageNum: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), pageSize: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), active: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)?, transactionCurrency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, transactionMode: [TransactionMode](../../-transaction-mode/index.md)?, countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [Options](../index.md)

## Constructors

| | |
|---|---|
| [RetrieveAvailablePaymentMethodsOptions](-retrieve-available-payment-methods-options.md) | [androidJvm]<br>constructor(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), pageNum: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), pageSize: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), active: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)?, transactionCurrency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, transactionMode: [TransactionMode](../../-transaction-mode/index.md)?, countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |

## Properties

| Name | Summary |
|---|---|
| [clientSecret](client-secret.md) | [androidJvm]<br>open override val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [getUrl](../../get-url.md) | [androidJvm]<br>fun [Options](../index.md).[getUrl](../../get-url.md)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [toAirwallexHttpRequest](../../to-airwallex-http-request.md) | [androidJvm]<br>fun [Options](../index.md).[toAirwallexHttpRequest](../../to-airwallex-http-request.md)(): [AirwallexHttpRequest](../../../com.airwallex.android.core.http/-airwallex-http-request/index.md) |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
