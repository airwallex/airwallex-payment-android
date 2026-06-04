//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexSession](index.md)

# AirwallexSession

abstract class [AirwallexSession](index.md)

#### Inheritors

| |
|---|
| [AirwallexPaymentSession](../-airwallex-payment-session/index.md) |
| [AirwallexRecurringSession](../-airwallex-recurring-session/index.md) |
| [AirwallexRecurringWithIntentSession](../-airwallex-recurring-with-intent-session/index.md) |
| [Session](../-session/index.md) |

## Constructors

| | |
|---|---|
| [AirwallexSession](-airwallex-session.md) | [androidJvm]<br>constructor() |

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>abstract val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)<br>Payment amount. This is the order amount you would like to charge your customer |
| [clientSecret](client-secret.md) | [androidJvm]<br>abstract val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The client secret for this session. Used for authenticating API requests. |
| [countryCode](country-code.md) | [androidJvm]<br>abstract val [countryCode](country-code.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Country code |
| [currency](currency.md) | [androidJvm]<br>abstract val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Amount currency |
| [customerId](customer-id.md) | [androidJvm]<br>abstract val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The Customer who is paying for this PaymentIntent. This field is not required if the Customer is unknown (guest checkout). But it is required if the PaymentIntent is created for recurring payment. |
| [googlePayOptions](google-pay-options.md) | [androidJvm]<br>abstract val [googlePayOptions](google-pay-options.md): [GooglePayOptions](../-google-pay-options/index.md)?<br>Google Pay options |
| [hidePaymentConsents](hide-payment-consents.md) | [androidJvm]<br>open val [hidePaymentConsents](hide-payment-consents.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Control whether saved cards are displayed on the list screen |
| [isBillingInformationRequired](is-billing-information-required.md) | [androidJvm]<br>abstract val [~~isBillingInformationRequired~~](is-billing-information-required.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Whether or not billing information is required for card payments. |
| [isEmailRequired](is-email-required.md) | [androidJvm]<br>abstract val [~~isEmailRequired~~](is-email-required.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Whether or not email is required for card payments |
| [isExpressCheckout](../is-express-checkout.md) | [androidJvm]<br>val [AirwallexSession](index.md).[isExpressCheckout](../is-express-checkout.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Indicates whether this session is configured for Express Checkout. Returns true when the session uses a PaymentIntentProvider for lazy payment intent creation. |
| [paymentMethods](payment-methods.md) | [androidJvm]<br>abstract val [paymentMethods](payment-methods.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;?<br>An array of payment method type names to limit the payment methods displayed on the list screen. Only available ones from your Airwallex account will be applied, any other ones will be ignored. Also the order of payment method list will follow the order of this array. API reference: https://www.airwallex.com/docs/api#/Payment_Acceptance/Config/_api_v1_pa_config_payment_method_types/get JSON Object field: items.name |
| [requiredBillingContactFields](required-billing-contact-fields.md) | [androidJvm]<br>open val [requiredBillingContactFields](required-billing-contact-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;?<br>Billing contact fields the SDK should collect (and validate) on the new-card payment screen. `null` means &quot;derive from the legacy [isBillingInformationRequired](is-billing-information-required.md) / [isEmailRequired](is-email-required.md) flags&quot; so unmodified integrations keep current behavior. An empty set hides the entire billing section. |
| [resolvedRequiredBillingContactFields](../resolved-required-billing-contact-fields.md) | [androidJvm]<br>val [AirwallexSession](index.md).[resolvedRequiredBillingContactFields](../resolved-required-billing-contact-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;<br>Effective set of billing contact fields, resolving `null` to the legacy boolean-derived defaults that match Android's pre-existing UI: |
| [returnUrl](return-url.md) | [androidJvm]<br>abstract val [returnUrl](return-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The URL to redirect your customer back to after they authenticate or cancel their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme. |
| [shipping](shipping.md) | [androidJvm]<br>abstract val [shipping](shipping.md): [Shipping](../../com.airwallex.android.core.model/-shipping/index.md)?<br>Shipping information |

## Functions

| Name | Summary |
|---|---|
| [bindToActivity](../bind-to-activity.md) | [androidJvm]<br>fun [AirwallexSession](index.md).[bindToActivity](../bind-to-activity.md)(activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html))<br>Binds this session's PaymentIntentProvider to an Activity lifecycle. This ensures the provider is cleaned up when the Activity is destroyed. Should be called once when the session starts being used with a specific Activity. |
| [resolvePaymentIntent](../resolve-payment-intent.md) | [androidJvm]<br>fun [AirwallexSession](index.md).[resolvePaymentIntent](../resolve-payment-intent.md)(callback: [PaymentIntentProvider.PaymentIntentCallback](../-payment-intent-provider/-payment-intent-callback/index.md))<br>Extension function to resolve PaymentIntent from session. If paymentIntent is available, calls callback immediately. If paymentIntentProvider is available (transient field), uses it to get the intent asynchronously. If paymentIntentProviderId is available (after binding), retrieves from repository. |
