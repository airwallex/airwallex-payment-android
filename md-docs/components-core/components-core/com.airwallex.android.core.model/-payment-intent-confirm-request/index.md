//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[PaymentIntentConfirmRequest](index.md)

# PaymentIntentConfirmRequest

[androidJvm]\
data class [PaymentIntentConfirmRequest](index.md) : [AirwallexRequestModel](../-airwallex-request-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

The request params to confirm [PaymentIntent](../-payment-intent/index.md)

## Types

| Name | Summary |
|---|---|
| [Builder](-builder/index.md) | [androidJvm]<br>class [Builder](-builder/index.md)(requestId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)) : [ObjectBuilder](../-object-builder/index.md)&lt;[PaymentIntentConfirmRequest](index.md)&gt; |

## Properties

| Name | Summary |
|---|---|
| [customerId](customer-id.md) | [androidJvm]<br>val [customerId](customer-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Customer who intends to pay for the payment intent |
| [device](device.md) | [androidJvm]<br>val [device](device.md): [Device](../-device/index.md)? = null<br>Device info |
| [integrationData](integration-data.md) | [androidJvm]<br>val [integrationData](integration-data.md): [IntegrationData](../-integration-data/index.md)? = null<br>Integration data |
| [paymentConsent](payment-consent.md) | [androidJvm]<br>val [paymentConsent](payment-consent.md): [PaymentConsentOptions](../-payment-consent-options/index.md)? = null<br>Payment consent options |
| [paymentConsentReference](payment-consent-reference.md) | [androidJvm]<br>val [paymentConsentReference](payment-consent-reference.md): [PaymentConsentReference](../-payment-consent-reference/index.md)? = null<br>Reference to an existing PaymentConsent |
| [paymentMethodOptions](payment-method-options.md) | [androidJvm]<br>val [paymentMethodOptions](payment-method-options.md): [PaymentMethodOptions](../-payment-method-options/index.md)? = null<br>Options for payment method |
| [paymentMethodRequest](payment-method-request.md) | [androidJvm]<br>val [paymentMethodRequest](payment-method-request.md): [PaymentMethodRequest](../-payment-method-request/index.md)? = null<br>The payment method that you want to confirm |
| [requestId](request-id.md) | [androidJvm]<br>val [requestId](request-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Unique request ID specified by the merchant |
| [returnUrl](return-url.md) | [androidJvm]<br>val [returnUrl](return-url.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>The URL to redirect your customer back to after they authenticate their payment on the PaymentMethod’s app or site. If you’d prefer to redirect to a mobile application, you can alternatively supply an application URI scheme. |
| [sdkType](../-airwallex-request-model/sdk-type.md) | [androidJvm]<br>open val [sdkType](../-airwallex-request-model/sdk-type.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [sdkVersion](../-airwallex-request-model/sdk-version.md) | [androidJvm]<br>open val [sdkVersion](../-airwallex-request-model/sdk-version.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [toParamMap](to-param-map.md) | [androidJvm]<br>open override fun [toParamMap](to-param-map.md)(): [Map](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-map/index.html)&lt;[String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), [Any](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-any/index.html)&gt; |
| [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
