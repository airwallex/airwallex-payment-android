//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[retrieveAvailablePaymentMethods](retrieve-available-payment-methods.md)

# retrieveAvailablePaymentMethods

[androidJvm]\
suspend fun [retrieveAvailablePaymentMethods](retrieve-available-payment-methods.md)(session: [AirwallexSession](../-airwallex-session/index.md), params: [RetrieveAvailablePaymentMethodParams](../../com.airwallex.android.core.model/-retrieve-available-payment-method-params/index.md)): [Page](../../com.airwallex.android.core.model/-page/index.md)&lt;[AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md)&gt;

Retrieve available payment methods

#### Parameters

androidJvm

| | |
|---|---|
| params | [RetrieveAvailablePaymentMethodParams](../../com.airwallex.android.core.model/-retrieve-available-payment-method-params/index.md) used to retrieve all [AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md) |

[androidJvm]\
fun [retrieveAvailablePaymentMethods](retrieve-available-payment-methods.md)(session: [AirwallexSession](../-airwallex-session/index.md), params: [RetrieveAvailablePaymentMethodParams](../../com.airwallex.android.core.model/-retrieve-available-payment-method-params/index.md), callback: [AirwallexCallback](../-airwallex-callback/index.md)&lt;[Page](../../com.airwallex.android.core.model/-page/index.md)&lt;[AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md)&gt;&gt;)

Retrieve available payment methods

#### Parameters

androidJvm

| | |
|---|---|
| session | The [AirwallexSession](../-airwallex-session/index.md) which contains session information for retrieving payment methods. |
| params | [RetrieveAvailablePaymentMethodParams](../../com.airwallex.android.core.model/-retrieve-available-payment-method-params/index.md) used to retrieve all [AvailablePaymentMethodType](../../com.airwallex.android.core.model/-available-payment-method-type/index.md) |
| callback | [AirwallexCallback](../-airwallex-callback/index.md) A callback interface to handle the success or failure of the network request. |
