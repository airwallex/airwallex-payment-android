//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[VerifyPaymentConsentParams](index.md)

# VerifyPaymentConsentParams

[androidJvm]\
data class [VerifyPaymentConsentParams](index.md)(val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, val currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null)

The params that used for verify [PaymentConsent](../-payment-consent/index.md)

## Constructors

| | |
|---|---|
| [VerifyPaymentConsentParams](-verify-payment-consent-params.md) | [androidJvm]<br>constructor(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null, currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, cvc: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) |

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md)(clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), paymentMethodType: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [ObjectBuilder](../-object-builder/index.md)&lt;[VerifyPaymentConsentParams](index.md)&gt; |
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null<br>The alternative amount of verification if zero amount is not acceptable for the provider. The transaction of this amount should be reverted once the verification process finished. Must be greater than 0. |
| [clientSecret](client-secret.md) | [androidJvm]<br>val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [currency](currency.md) | [androidJvm]<br>val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Currency of the initial PaymentIntent to verify the PaymentConsent. Three-letter ISO currency code. Must be a supported currency |
| [cvc](cvc.md) | [androidJvm]<br>val [cvc](cvc.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>When requires_cvc for the PaymentConsent is true, this attribute must be provided in order to confirm successfully |
| [paymentConsentId](payment-consent-id.md) | [androidJvm]<br>val [paymentConsentId](payment-consent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>PaymentConsent ID |
| [paymentMethodType](payment-method-type.md) | [androidJvm]<br>val [paymentMethodType](payment-method-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Type of the PaymentMethod. |
| [returnUrl](return-url.md) | [androidJvm]<br>val [returnUrl](return-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The URL to which your customer will be redirected after they verify PaymentConsent on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively provide an application URI scheme. |
