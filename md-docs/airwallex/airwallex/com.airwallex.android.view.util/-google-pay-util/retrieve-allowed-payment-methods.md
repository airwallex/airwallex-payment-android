//[airwallex](../../../index.md)/[com.airwallex.android.view.util](../index.md)/[GooglePayUtil](index.md)/[retrieveAllowedPaymentMethods](retrieve-allowed-payment-methods.md)

# retrieveAllowedPaymentMethods

[androidJvm]\
fun [retrieveAllowedPaymentMethods](retrieve-allowed-payment-methods.md)(googlePayOptions: [GooglePayOptions](../../../../components-core/components-core/com.airwallex.android.core/-google-pay-options/index.md), supportedCardSchemes: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CardScheme](../../../../components-core/components-core/com.airwallex.android.core.model/-card-scheme/index.md)&gt;?): [JSONArray](https://developer.android.com/reference/kotlin/org/json/JSONArray.html)?

An object describing accepted forms of payment by your app, used to determine a viewer's readiness to pay.

#### Return

API version and payment methods supported by the app.

#### See also

| | |
|---|---|
| IsReadyToPayRequest | (https://developers.google.com/pay/api/android/reference/object.IsReadyToPayRequest) |
