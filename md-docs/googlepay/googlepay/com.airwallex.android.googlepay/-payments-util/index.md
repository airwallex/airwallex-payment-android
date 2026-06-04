//[googlepay](../../../index.md)/[com.airwallex.android.googlepay](../index.md)/[PaymentsUtil](index.md)

# PaymentsUtil

[androidJvm]\
object [PaymentsUtil](index.md)

## Functions

| Name | Summary |
|---|---|
| [createPaymentsClient](create-payments-client.md) | [androidJvm]<br>fun [createPaymentsClient](create-payments-client.md)(context: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html)): PaymentsClient<br>Creates an instance of PaymentsClient for use in an [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html) using the environment and theme set in [Constants](../-constants/index.md). |
| [getBilling](get-billing.md) | [androidJvm]<br>fun [getBilling](get-billing.md)(payload: [JSONObject](https://developer.android.com/reference/kotlin/org/json/JSONObject.html)): [Billing](../../../../components-core/components-core/com.airwallex.android.core.model/-billing/index.md)? |
| [getPaymentDataRequest](get-payment-data-request.md) | [androidJvm]<br>fun [getPaymentDataRequest](get-payment-data-request.md)(price: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html), countryCode: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), googlePayOptions: [GooglePayOptions](../../../../components-core/components-core/com.airwallex.android.core/-google-pay-options/index.md), supportedCardSchemes: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CardScheme](../../../../components-core/components-core/com.airwallex.android.core.model/-card-scheme/index.md)&gt;?): [JSONObject](https://developer.android.com/reference/kotlin/org/json/JSONObject.html)?<br>An object describing information requested in a Google Pay payment sheet |
| [isReadyToPayRequest](is-ready-to-pay-request.md) | [androidJvm]<br>fun [isReadyToPayRequest](is-ready-to-pay-request.md)(googlePayOptions: [GooglePayOptions](../../../../components-core/components-core/com.airwallex.android.core/-google-pay-options/index.md), supportedCardSchemes: [List](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin.collections/-list/index.html)&lt;[CardScheme](../../../../components-core/components-core/com.airwallex.android.core.model/-card-scheme/index.md)&gt;?): [JSONObject](https://developer.android.com/reference/kotlin/org/json/JSONObject.html)?<br>An object describing accepted forms of payment by your app, used to determine a viewer's readiness to pay. |
