//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[Session](../index.md)/[Builder](index.md)

# Builder

[androidJvm]\
class [Builder](index.md) : [ObjectBuilder](../../../com.airwallex.android.core.model/-object-builder/index.md)&lt;[Session](../index.md)&gt;

## Constructors

| | |
|---|---|
| [Builder](-builder.md) | [androidJvm]<br>constructor(paymentIntent: [PaymentIntent](../../../com.airwallex.android.core.model/-payment-intent/index.md), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), googlePayOptions: [GooglePayOptions](../../-google-pay-options/index.md)? = null)<br>Constructor for static PaymentIntent<br>constructor(paymentIntentProvider: [PaymentIntentProvider](../../-payment-intent-provider/index.md), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, googlePayOptions: [GooglePayOptions](../../-google-pay-options/index.md)? = null)<br>Constructor for PaymentIntentProvider<br>constructor(paymentIntentSource: [PaymentIntentSource](../../-payment-intent-source/index.md), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, googlePayOptions: [GooglePayOptions](../../-google-pay-options/index.md)? = null)<br>Constructor for PaymentIntentSource (modern suspend-based version) |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [androidJvm]<br>open override fun [build](build.md)(): [Session](../index.md) |
| [setAutoCapture](set-auto-capture.md) | [androidJvm]<br>fun [setAutoCapture](set-auto-capture.md)(autoCapture: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [Session.Builder](index.md) |
| [setHidePaymentConsents](set-hide-payment-consents.md) | [androidJvm]<br>fun [setHidePaymentConsents](set-hide-payment-consents.md)(hidePaymentConsents: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [Session.Builder](index.md) |
| [setPaymentConsentOptions](set-payment-consent-options.md) | [androidJvm]<br>fun [setPaymentConsentOptions](set-payment-consent-options.md)(paymentConsentOptions: [PaymentConsentOptions](../../../com.airwallex.android.core.model/-payment-consent-options/index.md)?): [Session.Builder](index.md) |
| [setPaymentMethods](set-payment-methods.md) | [androidJvm]<br>fun [setPaymentMethods](set-payment-methods.md)(paymentMethods: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;?): [Session.Builder](index.md) |
| [setRequireBillingInformation](set-require-billing-information.md) | [androidJvm]<br>fun [~~setRequireBillingInformation~~](set-require-billing-information.md)(requireBillingInformation: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [Session.Builder](index.md) |
| [setRequiredBillingContactFields](set-required-billing-contact-fields.md) | [androidJvm]<br>fun [setRequiredBillingContactFields](set-required-billing-contact-fields.md)(fields: [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../../-required-billing-contact-field/index.md)&gt;?): [Session.Builder](index.md)<br>Configure which billing fields the new-card UI should collect and the headless checkout should validate. Pass `null` (the default) to derive from the legacy [setRequireBillingInformation](set-require-billing-information.md) / [setRequireEmail](set-require-email.md) flags. An empty set hides the entire billing section. |
| [setRequireEmail](set-require-email.md) | [androidJvm]<br>fun [~~setRequireEmail~~](set-require-email.md)(requireEmail: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [Session.Builder](index.md) |
| [setReturnUrl](set-return-url.md) | [androidJvm]<br>fun [setReturnUrl](set-return-url.md)(returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [Session.Builder](index.md) |
| [setShipping](set-shipping.md) | [androidJvm]<br>fun [setShipping](set-shipping.md)(shipping: [Shipping](../../../com.airwallex.android.core.model/-shipping/index.md)?): [Session.Builder](index.md) |
