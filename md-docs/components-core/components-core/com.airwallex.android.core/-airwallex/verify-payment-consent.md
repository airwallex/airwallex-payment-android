//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[verifyPaymentConsent](verify-payment-consent.md)

# verifyPaymentConsent

[androidJvm]\

@[UiThread](https://developer.android.com/reference/kotlin/androidx/annotation/UiThread.html)

fun [verifyPaymentConsent](verify-payment-consent.md)(device: [Device](../../com.airwallex.android.core.model/-device/index.md), params: [VerifyPaymentConsentParams](../../com.airwallex.android.core.model/-verify-payment-consent-params/index.md), listener: [Airwallex.PaymentResultListener](-payment-result-listener/index.md))

Verify a [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)

#### Parameters

androidJvm

| | |
|---|---|
| device | a [Device](../../com.airwallex.android.core.model/-device/index.md) object containing device information for fingerprinting |
| params | [VerifyPaymentConsentParams](../../com.airwallex.android.core.model/-verify-payment-consent-params/index.md) used to verify the [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md) |
| listener | a [PaymentListener](-payment-listener/index.md) to receive the response or error |
