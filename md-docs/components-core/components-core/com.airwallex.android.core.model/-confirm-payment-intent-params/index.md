//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[ConfirmPaymentIntentParams](index.md)

# ConfirmPaymentIntentParams

[androidJvm]\
data class [ConfirmPaymentIntentParams](index.md) : [AbstractPaymentIntentParams](../-abstract-payment-intent-params/index.md)

The params that used for confirm [PaymentIntent](../-payment-intent/index.md)

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md)(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [ObjectBuilder](../-object-builder/index.md)&lt;[ConfirmPaymentIntentParams](index.md)&gt; |
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [additionalInfo](additional-info.md) | [androidJvm]<br>val [additionalInfo](additional-info.md): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null<br>AdditionalInfo (used by LPMs) |
| [autoCapture](auto-capture.md) | [androidJvm]<br>val [autoCapture](auto-capture.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true<br>Indicate if the payment shall be captured immediately after authorized. Only applicable to Card. Default: true |
| [clientSecret](client-secret.md) | [androidJvm]<br>open override val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>The client secret of [PaymentIntent](../-payment-intent/index.md) |
| [countryCode](country-code.md) | [androidJvm]<br>val [countryCode](country-code.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Country Code |
| [currency](currency.md) | [androidJvm]<br>val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Currency |
| [customerId](customer-id.md) | [androidJvm]<br>val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>optional, the ID of a Customer. |
| [cvc](cvc.md) | [androidJvm]<br>val [cvc](cvc.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>CVC |
| [flow](flow.md) | [androidJvm]<br>val [flow](flow.md): [AirwallexPaymentRequestFlow](../-airwallex-payment-request-flow/index.md)? = null<br>Payment Request Flow |
| [paymentConsentId](payment-consent-id.md) | [androidJvm]<br>val [paymentConsentId](payment-consent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique identifier of this [PaymentConsent](../-payment-consent/index.md) |
| [paymentConsentOptions](payment-consent-options.md) | [androidJvm]<br>val [paymentConsentOptions](payment-consent-options.md): [PaymentConsentOptions](../-payment-consent-options/index.md)? = null<br>Payment consent options for new unified flow |
| [paymentIntentId](payment-intent-id.md) | [androidJvm]<br>open override val [paymentIntentId](payment-intent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>ID of [PaymentIntent](../-payment-intent/index.md) |
| [paymentMethod](payment-method.md) | [androidJvm]<br>val [paymentMethod](payment-method.md): [PaymentMethod](../-payment-method/index.md)? = null<br>Payment Method |
| [paymentMethodType](payment-method-type.md) | [androidJvm]<br>val [paymentMethodType](payment-method-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Payment method type |
| [returnUrl](return-url.md) | [androidJvm]<br>val [returnUrl](return-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The URL to redirect your customer back to after they authenticate their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme. |
