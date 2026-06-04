//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexApiRepository](index.md)

# AirwallexApiRepository

[androidJvm]\
class [AirwallexApiRepository](index.md) : [ApiRepository](../-api-repository/index.md)

The implementation of [ApiRepository](../-api-repository/index.md) to request the Airwallex API.

## Constructors

| | |
|---|---|
| [AirwallexApiRepository](-airwallex-api-repository.md) | [androidJvm]<br>constructor() |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [confirmPaymentIntent](confirm-payment-intent.md) | [androidJvm]<br>open suspend override fun [confirmPaymentIntent](confirm-payment-intent.md)(options: [Options.ConfirmPaymentIntentOptions](../../com.airwallex.android.core.model/-options/-confirm-payment-intent-options/index.md)): [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)?<br>Confirm a PaymentIntent using the provided [Options](../../com.airwallex.android.core.model/-options/index.md) |
| [continuePaymentIntent](continue-payment-intent.md) | [androidJvm]<br>open suspend override fun [continuePaymentIntent](continue-payment-intent.md)(options: [Options.ContinuePaymentIntentOptions](../../com.airwallex.android.core.model/-options/-continue-payment-intent-options/index.md)): [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)?<br>Continue a PaymentIntent using the provided [Options](../../com.airwallex.android.core.model/-options/index.md) |
| [createPaymentConsent](create-payment-consent.md) | [androidJvm]<br>open suspend override fun [createPaymentConsent](create-payment-consent.md)(options: [Options.CreatePaymentConsentOptions](../../com.airwallex.android.core.model/-options/-create-payment-consent-options/index.md)): [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)?<br>Create a PaymentConsent |
| [createPaymentMethod](create-payment-method.md) | [androidJvm]<br>open suspend override fun [createPaymentMethod](create-payment-method.md)(options: [Options.CreatePaymentMethodOptions](../../com.airwallex.android.core.model/-options/-create-payment-method-options/index.md)): [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md)?<br>Create a Airwallex [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md) using [Options](../../com.airwallex.android.core.model/-options/index.md) |
| [disablePaymentConsent](disable-payment-consent.md) | [androidJvm]<br>open suspend override fun [disablePaymentConsent](disable-payment-consent.md)(options: [Options.DisablePaymentConsentOptions](../../com.airwallex.android.core.model/-options/-disable-payment-consent-options/index.md)): [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)?<br>Disable a PaymentConsent |
| [executeMockWeChat](execute-mock-we-chat.md) | [androidJvm]<br>open suspend override fun [executeMockWeChat](execute-mock-we-chat.md)(mockWeChatUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html))<br>Execute mock wechat (Just for demo env) |
| [retrieveAvailablePaymentConsents](retrieve-available-payment-consents.md) | [androidJvm]<br>open suspend override fun [retrieveAvailablePaymentConsents](retrieve-available-payment-consents.md)(options: [Options.RetrieveAvailablePaymentConsentsOptions](../../com.airwallex.android.core.model/-options/-retrieve-available-payment-consents-options/index.md)): [Page](../../com.airwallex.android.core.model/-page/index.md)&lt;[PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)&gt;?<br>Retrieve available payment consents |
| [retrieveAvailablePaymentMethods](retrieve-available-payment-methods.md) | [androidJvm]<br>open suspend override fun [retrieveAvailablePaymentMethods](retrieve-available-payment-methods.md)(options: [Options.RetrieveAvailablePaymentMethodsOptions](../../com.airwallex.android.core.model/-options/-retrieve-available-payment-methods-options/index.md)): [Page](../../com.airwallex.android.core.model/-page/index.md)&lt;[AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md)&gt;?<br>Retrieve available payment method types |
| [retrieveBanks](retrieve-banks.md) | [androidJvm]<br>open suspend override fun [retrieveBanks](retrieve-banks.md)(options: [Options.RetrieveBankOptions](../../com.airwallex.android.core.model/-options/-retrieve-bank-options/index.md)): [BankResponse](../../com.airwallex.android.core.model/-bank-response/index.md)?<br>Retrieve banks of payment method |
| [retrievePaymentConsent](retrieve-payment-consent.md) | [androidJvm]<br>open suspend override fun [retrievePaymentConsent](retrieve-payment-consent.md)(options: [Options.RetrievePaymentConsentOptions](../../com.airwallex.android.core.model/-options/-retrieve-payment-consent-options/index.md)): [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)?<br>Retrieve a PaymentConsent |
| [retrievePaymentIntent](retrieve-payment-intent.md) | [androidJvm]<br>open suspend override fun [retrievePaymentIntent](retrieve-payment-intent.md)(options: [Options.RetrievePaymentIntentOptions](../../com.airwallex.android.core.model/-options/-retrieve-payment-intent-options/index.md)): [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)?<br>Retrieve a PaymentIntent using the provided [Options](../../com.airwallex.android.core.model/-options/index.md) |
| [retrievePaymentMethodTypeInfo](retrieve-payment-method-type-info.md) | [androidJvm]<br>open suspend override fun [retrievePaymentMethodTypeInfo](retrieve-payment-method-type-info.md)(options: [Options.RetrievePaymentMethodTypeInfoOptions](../../com.airwallex.android.core.model/-options/-retrieve-payment-method-type-info-options/index.md)): [PaymentMethodTypeInfo](../../com.airwallex.android.core.model/-payment-method-type-info/index.md)?<br>Retrieve payment method detail |
| [verifyPaymentConsent](verify-payment-consent.md) | [androidJvm]<br>open suspend override fun [verifyPaymentConsent](verify-payment-consent.md)(options: [Options.VerifyPaymentConsentOptions](../../com.airwallex.android.core.model/-options/-verify-payment-consent-options/index.md)): [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)?<br>Verify a PaymentConsent |
