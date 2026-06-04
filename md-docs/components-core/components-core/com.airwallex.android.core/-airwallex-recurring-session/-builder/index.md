//[components-core](../../../../index.md)/[com.airwallex.android.core](../../index.md)/[AirwallexRecurringSession](../index.md)/[Builder](index.md)

# Builder

[androidJvm]\
class [Builder](index.md)(customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html), nextTriggerBy: [PaymentConsent.NextTriggeredBy](../../../com.airwallex.android.core.model/-payment-consent/-next-triggered-by/index.md), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [ObjectBuilder](../../../com.airwallex.android.core.model/-object-builder/index.md)&lt;[AirwallexRecurringSession](../index.md)&gt;

## Constructors

| | |
|---|---|
| [Builder](-builder.md) | [androidJvm]<br>constructor(customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html), nextTriggerBy: [PaymentConsent.NextTriggeredBy](../../../com.airwallex.android.core.model/-payment-consent/-next-triggered-by/index.md), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) |

## Functions

| Name | Summary |
|---|---|
| [build](build.md) | [androidJvm]<br>open override fun [build](build.md)(): [AirwallexRecurringSession](../index.md) |
| [setGooglePayOptions](set-google-pay-options.md) | [androidJvm]<br>fun [setGooglePayOptions](set-google-pay-options.md)(googlePayOptions: [GooglePayOptions](../../-google-pay-options/index.md)?): [AirwallexRecurringSession.Builder](index.md) |
| [setMerchantTriggerReason](set-merchant-trigger-reason.md) | [androidJvm]<br>fun [setMerchantTriggerReason](set-merchant-trigger-reason.md)(merchantTriggerReason: [PaymentConsent.MerchantTriggerReason](../../../com.airwallex.android.core.model/-payment-consent/-merchant-trigger-reason/index.md)): [AirwallexRecurringSession.Builder](index.md) |
| [setPaymentMethods](set-payment-methods.md) | [androidJvm]<br>fun [setPaymentMethods](set-payment-methods.md)(paymentMethods: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;?): [AirwallexRecurringSession.Builder](index.md) |
| [setRequireBillingInformation](set-require-billing-information.md) | [androidJvm]<br>fun [~~setRequireBillingInformation~~](set-require-billing-information.md)(requiresBillingInformation: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [AirwallexRecurringSession.Builder](index.md) |
| [setRequireCvc](set-require-cvc.md) | [androidJvm]<br>fun [setRequireCvc](set-require-cvc.md)(requiresCVC: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [AirwallexRecurringSession.Builder](index.md) |
| [setRequiredBillingContactFields](set-required-billing-contact-fields.md) | [androidJvm]<br>fun [setRequiredBillingContactFields](set-required-billing-contact-fields.md)(fields: [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../../-required-billing-contact-field/index.md)&gt;?): [AirwallexRecurringSession.Builder](index.md)<br>Configure which billing fields the new-card UI should collect and the headless checkout should validate. Pass `null` (the default) to derive from the legacy [setRequireBillingInformation](set-require-billing-information.md) / [setRequireEmail](set-require-email.md) flags. An empty set hides the entire billing section. |
| [setRequireEmail](set-require-email.md) | [androidJvm]<br>fun [~~setRequireEmail~~](set-require-email.md)(requiresEmail: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)): [AirwallexRecurringSession.Builder](index.md) |
| [setReturnUrl](set-return-url.md) | [androidJvm]<br>fun [setReturnUrl](set-return-url.md)(returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?): [AirwallexRecurringSession.Builder](index.md) |
| [setShipping](set-shipping.md) | [androidJvm]<br>fun [setShipping](set-shipping.md)(shipping: [Shipping](../../../com.airwallex.android.core.model/-shipping/index.md)?): [AirwallexRecurringSession.Builder](index.md) |
