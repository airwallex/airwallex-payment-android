//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexPaymentSession](index.md)

# AirwallexPaymentSession

[androidJvm]\
class [~~AirwallexPaymentSession~~](index.md) : [AirwallexSession](../-airwallex-session/index.md), [PaymentIntentResolvableSession](../../../../components-core/com.airwallex.android.core/-payment-intent-resolvable-session/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)---

### Deprecated

Use Session instead. AirwallexPaymentSession will be removed in a future version.

#### Replace with

```kotlin
import com.airwallex.android.core.Session

```
```kotlin
Session.Builder(paymentIntent, countryCode, googlePayOptions)
```
---

For one-off payment

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md) : [ObjectBuilder](../../com.airwallex.android.core.model/-object-builder/index.md)&lt;[AirwallexPaymentSession](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>open override val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)<br>Payment amount. This is the order amount you would like to charge your customer. required. |
| [autoCapture](auto-capture.md) | [androidJvm]<br>val [autoCapture](auto-capture.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true<br>Indicate if the payment shall be captured immediately after authorized. Only applicable to Card. Default: true |
| [clientSecret](client-secret.md) | [androidJvm]<br>open override val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The client secret for this session. Used for authenticating API requests. |
| [countryCode](country-code.md) | [androidJvm]<br>open override val [countryCode](country-code.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Country code |
| [currency](currency.md) | [androidJvm]<br>open override val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)<br>Amount currency. required. |
| [customerId](customer-id.md) | [androidJvm]<br>open override val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The Customer who is paying for this PaymentIntent. This field is not required if the Customer is unknown (guest checkout). |
| [googlePayOptions](google-pay-options.md) | [androidJvm]<br>open override val [googlePayOptions](google-pay-options.md): [GooglePayOptions](../-google-pay-options/index.md)? = null<br>Google Pay options |
| [hidePaymentConsents](hide-payment-consents.md) | [androidJvm]<br>open override val [hidePaymentConsents](hide-payment-consents.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false<br>control whether saved cards are displayed on the list screen |
| [isBillingInformationRequired](is-billing-information-required.md) | [androidJvm]<br>open override val [isBillingInformationRequired](is-billing-information-required.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = true<br>Whether or not billing information is required for card payments. When set to `false`, any billing information will be ignored. |
| [isEmailRequired](is-email-required.md) | [androidJvm]<br>open override val [isEmailRequired](is-email-required.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false<br>Whether or not email is required for card payments. Set to 'true' if you'd like to collect customers' email |
| [isExpressCheckout](../is-express-checkout.md) | [androidJvm]<br>val [AirwallexSession](../-airwallex-session/index.md).[isExpressCheckout](../is-express-checkout.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html)<br>Indicates whether this session is configured for Express Checkout. Returns true when the session uses a PaymentIntentProvider for lazy payment intent creation. |
| [paymentIntent](payment-intent.md) | [androidJvm]<br>open override val [paymentIntent](payment-intent.md): [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)?<br>The [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md) object, optional when using paymentIntentProvider. |
| [paymentIntentProvider](payment-intent-provider.md) | [androidJvm]<br>@[Transient](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.jvm/-transient/index.html)<br>open override var [paymentIntentProvider](payment-intent-provider.md): [PaymentIntentProvider](../-payment-intent-provider/index.md)?<br>PaymentIntentProvider instance. This field is transient and not parceled. It is stored only in memory and bound to the Activity lifecycle via PaymentIntentProviderRepository. |
| [paymentIntentProviderId](payment-intent-provider-id.md) | [androidJvm]<br>open override var [paymentIntentProviderId](payment-intent-provider-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>Internal identifier for the PaymentIntentProvider stored in the repository. This is set when bindToActivity is called. |
| [paymentMethods](payment-methods.md) | [androidJvm]<br>open override val [paymentMethods](payment-methods.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? = null<br>An array of payment method type names to limit the payment methods displayed on the list screen. Only available ones from your Airwallex account will be applied, any other ones will be ignored. Also the order of payment method list will follow the order of this array. API reference: https://www.airwallex.com/docs/api#/Payment_Acceptance/Config/_api_v1_pa_config_payment_method_types/get JSON Object field: items.name |
| [requiredBillingContactFields](required-billing-contact-fields.md) | [androidJvm]<br>open override val [requiredBillingContactFields](required-billing-contact-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;? = null<br>Billing contact fields the SDK should collect on the new-card screen. `null` preserves legacy behavior (derived from [isBillingInformationRequired](is-billing-information-required.md) / [isEmailRequired](is-email-required.md)). |
| [resolvedRequiredBillingContactFields](../resolved-required-billing-contact-fields.md) | [androidJvm]<br>val [AirwallexSession](../-airwallex-session/index.md).[resolvedRequiredBillingContactFields](../resolved-required-billing-contact-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;<br>Effective set of billing contact fields, resolving `null` to the legacy boolean-derived defaults that match Android's pre-existing UI: |
| [returnUrl](return-url.md) | [androidJvm]<br>open override val [returnUrl](return-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>The URL to redirect your customer back to after they authenticate or cancel their payment on the PaymentMethod's app or site. If you'd prefer to redirect to a mobile application, you can alternatively supply an application URI scheme. |
| [shipping](shipping.md) | [androidJvm]<br>open override val [shipping](shipping.md): [Shipping](../../com.airwallex.android.core.model/-shipping/index.md)? = null<br>Shipping information. optional |

## Functions

| Name | Summary |
|---|---|
| [bindToActivity](../bind-to-activity.md) | [androidJvm]<br>fun [AirwallexSession](../-airwallex-session/index.md).[bindToActivity](../bind-to-activity.md)(activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html))<br>Binds this session's PaymentIntentProvider to an Activity lifecycle. This ensures the provider is cleaned up when the Activity is destroyed. Should be called once when the session starts being used with a specific Activity. |
| [convertToSession](../../com.airwallex.android.core.extension/convert-to-session.md) | [androidJvm]<br>fun [AirwallexPaymentSession](index.md).[convertToSession](../../com.airwallex.android.core.extension/convert-to-session.md)(): [Session](../-session/index.md)<br>Converts [AirwallexPaymentSession](index.md) (legacy one-off payment session) to the new [Session](../-session/index.md) type. Preserves both paymentIntent and paymentIntentProvider for full compatibility. |
| [describeContents](../../com.airwallex.android.core.model/-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../com.airwallex.android.core.model/-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [resolvePaymentIntent](../resolve-payment-intent.md) | [androidJvm]<br>fun [AirwallexSession](../-airwallex-session/index.md).[resolvePaymentIntent](../resolve-payment-intent.md)(callback: [PaymentIntentProvider.PaymentIntentCallback](../-payment-intent-provider/-payment-intent-callback/index.md))<br>Extension function to resolve PaymentIntent from session. If paymentIntent is available, calls callback immediately. If paymentIntentProvider is available (transient field), uses it to get the intent asynchronously. If paymentIntentProviderId is available (after binding), retrieves from repository. |
| [writeToParcel](../../com.airwallex.android.core.model/-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../com.airwallex.android.core.model/-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
