//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[AirwallexRequestModel](index.md)

# AirwallexRequestModel

interface [AirwallexRequestModel](index.md)

#### Inheritors

| |
|---|
| [Address](../-address/index.md) |
| [AirwallexPaymentRequest](../-airwallex-payment-request/index.md) |
| [Billing](../-billing/index.md) |
| [Device](../-device/index.md) |
| [IntegrationData](../-integration-data/index.md) |
| [PaymentConsentCreateRequest](../-payment-consent-create-request/index.md) |
| [PaymentConsentDisableRequest](../-payment-consent-disable-request/index.md) |
| [PaymentConsentOptions](../-payment-consent-options/index.md) |
| [TermsOfUse](../-payment-consent-options/-terms-of-use/index.md) |
| [PaymentSchedule](../-payment-consent-options/-payment-schedule/index.md) |
| [PaymentConsentReference](../-payment-consent-reference/index.md) |
| [PaymentConsentVerifyRequest](../-payment-consent-verify-request/index.md) |
| [VerificationOptions](../-payment-consent-verify-request/-verification-options/index.md) |
| [ThirdPartVerificationOptions](../-payment-consent-verify-request/-third-part-verification-options/index.md) |
| [CardVerificationOptions](../-payment-consent-verify-request/-card-verification-options/index.md) |
| [PaymentIntentConfirmRequest](../-payment-intent-confirm-request/index.md) |
| [PaymentIntentContinueRequest](../-payment-intent-continue-request/index.md) |
| [GooglePay](../-payment-method/-google-pay/index.md) |
| [Card](../-payment-method/-card/index.md) |
| [PaymentMethodCreateRequest](../-payment-method-create-request/index.md) |
| [PaymentMethodOptions](../-payment-method-options/index.md) |
| [CardOptions](../-payment-method-options/-card-options/index.md) |
| [PaymentMethodReference](../-payment-method-reference/index.md) |
| [PaymentMethodRequest](../-payment-method-request/index.md) |
| [PhysicalProduct](../-physical-product/index.md) |
| [PurchaseOrder](../-purchase-order/index.md) |
| [Shipping](../-shipping/index.md) |
| [ThreeDSecure](../-three-d-secure/index.md) |
| [TrackerRequest](../-tracker-request/index.md) |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [sdkType](sdk-type.md) | [androidJvm]<br>open val [sdkType](sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](sdk-version.md) | [androidJvm]<br>open val [sdkVersion](sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [toParamMap](to-param-map.md) | [androidJvm]<br>abstract fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
