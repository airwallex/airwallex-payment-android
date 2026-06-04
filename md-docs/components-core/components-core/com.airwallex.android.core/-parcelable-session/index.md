//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[ParcelableSession](index.md)

# ParcelableSession

[androidJvm]\
@[RestrictTo](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.html)(value = [[RestrictTo.Scope.LIBRARY_GROUP](https://developer.android.com/reference/kotlin/androidx/annotation/RestrictTo.Scope.LIBRARY_GROUP.html)])

class [ParcelableSession](index.md)(val paymentIntent: [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)?, val paymentIntentProviderId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val paymentConsentOptions: [PaymentConsentOptions](../../com.airwallex.android.core.model/-payment-consent-options/index.md)?, val currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html), val shipping: [Shipping](../../com.airwallex.android.core.model/-shipping/index.md)?, val isBillingInformationRequired: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), val isEmailRequired: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), val customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val googlePayOptions: [GooglePayOptions](../-google-pay-options/index.md)?, val paymentMethods: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;?, val autoCapture: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), val hidePaymentConsents: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), val requiredBillingContactFields: [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;?) : [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [ParcelableSession](-parcelable-session.md) | [androidJvm]<br>constructor(paymentIntent: [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)?, paymentIntentProviderId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, paymentConsentOptions: [PaymentConsentOptions](../../com.airwallex.android.core.model/-payment-consent-options/index.md)?, currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html), shipping: [Shipping](../../com.airwallex.android.core.model/-shipping/index.md)?, isBillingInformationRequired: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), isEmailRequired: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), customerId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, returnUrl: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, googlePayOptions: [GooglePayOptions](../-google-pay-options/index.md)?, paymentMethods: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;?, autoCapture: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), hidePaymentConsents: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html), requiredBillingContactFields: [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;?) |

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html) |
| [autoCapture](auto-capture.md) | [androidJvm]<br>val [autoCapture](auto-capture.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [countryCode](country-code.md) | [androidJvm]<br>val [countryCode](country-code.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [currency](currency.md) | [androidJvm]<br>val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [customerId](customer-id.md) | [androidJvm]<br>val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [googlePayOptions](google-pay-options.md) | [androidJvm]<br>val [googlePayOptions](google-pay-options.md): [GooglePayOptions](../-google-pay-options/index.md)? |
| [hidePaymentConsents](hide-payment-consents.md) | [androidJvm]<br>val [hidePaymentConsents](hide-payment-consents.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [isBillingInformationRequired](is-billing-information-required.md) | [androidJvm]<br>val [isBillingInformationRequired](is-billing-information-required.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [isEmailRequired](is-email-required.md) | [androidJvm]<br>val [isEmailRequired](is-email-required.md): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [paymentConsentOptions](payment-consent-options.md) | [androidJvm]<br>val [paymentConsentOptions](payment-consent-options.md): [PaymentConsentOptions](../../com.airwallex.android.core.model/-payment-consent-options/index.md)? |
| [paymentIntent](payment-intent.md) | [androidJvm]<br>val [paymentIntent](payment-intent.md): [PaymentIntent](../../com.airwallex.android.core.model/-payment-intent/index.md)? |
| [paymentIntentProviderId](payment-intent-provider-id.md) | [androidJvm]<br>val [paymentIntentProviderId](payment-intent-provider-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [paymentMethods](payment-methods.md) | [androidJvm]<br>val [paymentMethods](payment-methods.md): [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)&gt;? |
| [requiredBillingContactFields](required-billing-contact-fields.md) | [androidJvm]<br>val [requiredBillingContactFields](required-billing-contact-fields.md): [Set](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-set/index.html)&lt;[RequiredBillingContactField](../-required-billing-contact-field/index.md)&gt;? |
| [returnUrl](return-url.md) | [androidJvm]<br>val [returnUrl](return-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [shipping](shipping.md) | [androidJvm]<br>val [shipping](shipping.md): [Shipping](../../com.airwallex.android.core.model/-shipping/index.md)? |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../com.airwallex.android.core.model/-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../com.airwallex.android.core.model/-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toSession](to-session.md) | [androidJvm]<br>fun [toSession](to-session.md)(): [Session](../-session/index.md) |
| [writeToParcel](../../com.airwallex.android.core.model/-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../com.airwallex.android.core.model/-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
