//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[Options](index.md)

# Options

sealed class [Options](index.md) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

#### Inheritors

| |
|---|
| [RetrievePaymentIntentOptions](-retrieve-payment-intent-options/index.md) |
| [ConfirmPaymentIntentOptions](-confirm-payment-intent-options/index.md) |
| [ContinuePaymentIntentOptions](-continue-payment-intent-options/index.md) |
| [CreatePaymentMethodOptions](-create-payment-method-options/index.md) |
| [CreatePaymentConsentOptions](-create-payment-consent-options/index.md) |
| [VerifyPaymentConsentOptions](-verify-payment-consent-options/index.md) |
| [DisablePaymentConsentOptions](-disable-payment-consent-options/index.md) |
| [RetrievePaymentConsentOptions](-retrieve-payment-consent-options/index.md) |
| [RetrieveAvailablePaymentConsentsOptions](-retrieve-available-payment-consents-options/index.md) |
| [RetrieveAvailablePaymentMethodsOptions](-retrieve-available-payment-methods-options/index.md) |
| [RetrievePaymentMethodTypeInfoOptions](-retrieve-payment-method-type-info-options/index.md) |
| [RetrieveBankOptions](-retrieve-bank-options/index.md) |

## Types

| Name | Summary |
|---|---|
| [ConfirmPaymentIntentOptions](-confirm-payment-intent-options/index.md) | [androidJvm]<br>data class [ConfirmPaymentIntentOptions](-confirm-payment-intent-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), request: [PaymentIntentConfirmRequest](../-payment-intent-confirm-request/index.md)) : [Options](index.md) |
| [ContinuePaymentIntentOptions](-continue-payment-intent-options/index.md) | [androidJvm]<br>data class [ContinuePaymentIntentOptions](-continue-payment-intent-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), request: [PaymentIntentContinueRequest](../-payment-intent-continue-request/index.md)) : [Options](index.md) |
| [CreatePaymentConsentOptions](-create-payment-consent-options/index.md) | [androidJvm]<br>data class [CreatePaymentConsentOptions](-create-payment-consent-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), request: [PaymentConsentCreateRequest](../-payment-consent-create-request/index.md)) : [Options](index.md) |
| [CreatePaymentMethodOptions](-create-payment-method-options/index.md) | [androidJvm]<br>data class [CreatePaymentMethodOptions](-create-payment-method-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), request: [PaymentMethodCreateRequest](../-payment-method-create-request/index.md)) : [Options](index.md) |
| [DisablePaymentConsentOptions](-disable-payment-consent-options/index.md) | [androidJvm]<br>data class [DisablePaymentConsentOptions](-disable-payment-consent-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), request: [PaymentConsentDisableRequest](../-payment-consent-disable-request/index.md)) : [Options](index.md) |
| [RetrieveAvailablePaymentConsentsOptions](-retrieve-available-payment-consents-options/index.md) | [androidJvm]<br>data class [RetrieveAvailablePaymentConsentsOptions](-retrieve-available-payment-consents-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../-payment-consent/-merchant-trigger-reason/index.md)?, nextTriggeredBy: [PaymentConsent.NextTriggeredBy](../-payment-consent/-next-triggered-by/index.md)?, status: [PaymentConsent.PaymentConsentStatus](../-payment-consent/-payment-consent-status/index.md)?, pageNum: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), pageSize: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) : [Options](index.md) |
| [RetrieveAvailablePaymentMethodsOptions](-retrieve-available-payment-methods-options/index.md) | [androidJvm]<br>data class [RetrieveAvailablePaymentMethodsOptions](-retrieve-available-payment-methods-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), pageNum: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), pageSize: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), active: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)?, transactionCurrency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, transactionMode: [TransactionMode](../-transaction-mode/index.md)?, countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [Options](index.md) |
| [RetrieveBankOptions](-retrieve-bank-options/index.md) | [androidJvm]<br>data class [RetrieveBankOptions](-retrieve-bank-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), flow: [AirwallexPaymentRequestFlow](../-airwallex-payment-request-flow/index.md)?, transactionMode: [TransactionMode](../-transaction-mode/index.md)?, countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, openId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [Options](index.md) |
| [RetrievePaymentConsentOptions](-retrieve-payment-consent-options/index.md) | [androidJvm]<br>data class [RetrievePaymentConsentOptions](-retrieve-payment-consent-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [Options](index.md) |
| [RetrievePaymentIntentOptions](-retrieve-payment-intent-options/index.md) | [androidJvm]<br>data class [RetrievePaymentIntentOptions](-retrieve-payment-intent-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [Options](index.md) |
| [RetrievePaymentMethodTypeInfoOptions](-retrieve-payment-method-type-info-options/index.md) | [androidJvm]<br>data class [RetrievePaymentMethodTypeInfoOptions](-retrieve-payment-method-type-info-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), flow: [AirwallexPaymentRequestFlow](../-airwallex-payment-request-flow/index.md)?, transactionMode: [TransactionMode](../-transaction-mode/index.md)?, countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, openId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) : [Options](index.md) |
| [VerifyPaymentConsentOptions](-verify-payment-consent-options/index.md) | [androidJvm]<br>data class [VerifyPaymentConsentOptions](-verify-payment-consent-options/index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), request: [PaymentConsentVerifyRequest](../-payment-consent-verify-request/index.md)) : [Options](index.md) |

## Properties

| Name | Summary |
|---|---|
| [clientSecret](client-secret.md) | [androidJvm]<br>open val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [getUrl](../get-url.md) | [androidJvm]<br>fun [Options](index.md).[getUrl](../get-url.md)(): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [toAirwallexHttpRequest](../to-airwallex-http-request.md) | [androidJvm]<br>fun [Options](index.md).[toAirwallexHttpRequest](../to-airwallex-http-request.md)(): [AirwallexHttpRequest](../../com.airwallex.android.core.http/-airwallex-http-request/index.md) |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
