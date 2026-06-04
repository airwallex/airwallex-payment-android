//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[AirwallexPaymentManager](index.md)

# AirwallexPaymentManager

[androidJvm]\
class [AirwallexPaymentManager](index.md)(repository: [ApiRepository](../-api-repository/index.md)) : [PaymentManager](../-payment-manager/index.md)

## Constructors

| | |
|---|---|
| [AirwallexPaymentManager](-airwallex-payment-manager.md) | [androidJvm]<br>constructor(repository: [ApiRepository](../-api-repository/index.md)) |

## Functions

| Name | Summary |
|---|---|
| [buildDeviceInfo](build-device-info.md) | [androidJvm]<br>open override fun [buildDeviceInfo](build-device-info.md)(deviceId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)): [Device](../../com.airwallex.android.core.model/-device/index.md) |
| [createPaymentConsent](create-payment-consent.md) | [androidJvm]<br>open suspend override fun [createPaymentConsent](create-payment-consent.md)(options: [Options.CreatePaymentConsentOptions](../../com.airwallex.android.core.model/-options/-create-payment-consent-options/index.md)): [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md) |
| [createPaymentMethod](create-payment-method.md) | [androidJvm]<br>open suspend override fun [createPaymentMethod](create-payment-method.md)(options: [Options.CreatePaymentMethodOptions](../../com.airwallex.android.core.model/-options/-create-payment-method-options/index.md)): [PaymentMethod](../../com.airwallex.android.core.model/-payment-method/index.md) |
| [retrieveAvailablePaymentConsents](retrieve-available-payment-consents.md) | [androidJvm]<br>open suspend override fun [retrieveAvailablePaymentConsents](retrieve-available-payment-consents.md)(options: [Options.RetrieveAvailablePaymentConsentsOptions](../../com.airwallex.android.core.model/-options/-retrieve-available-payment-consents-options/index.md)): [Page](../../com.airwallex.android.core.model/-page/index.md)&lt;[PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md)&gt; |
| [retrieveAvailablePaymentMethods](retrieve-available-payment-methods.md) | [androidJvm]<br>open suspend override fun [retrieveAvailablePaymentMethods](retrieve-available-payment-methods.md)(options: [Options.RetrieveAvailablePaymentMethodsOptions](../../com.airwallex.android.core.model/-options/-retrieve-available-payment-methods-options/index.md)): [Page](../../com.airwallex.android.core.model/-page/index.md)&lt;[AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md)&gt; |
| [startOperation](start-operation.md) | [androidJvm]<br>open override fun &lt;[T](start-operation.md)&gt; [startOperation](start-operation.md)(options: [Options](../../com.airwallex.android.core.model/-options/index.md), listener: [Airwallex.PaymentListener](../-airwallex/-payment-listener/index.md)&lt;[T](start-operation.md)&gt;) |
