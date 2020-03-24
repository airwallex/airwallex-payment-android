# Getting started with the Android SDK
This section mainly introduces the main process of integrating Airwallex Android SDK. This guide assumes that you are an Android developer and familiar with Android Studio and Gradle.

## Introduction
With Airwallex Android SDK, you can integrate Airwallex Wechat Pay into your application.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to include the Airwallex Android SDK in your Android project.

## Integration 
Getting started with the Android SDK requires the following steps:

### Step 1: Install the SDK
The Android SDK is compatible with apps supporting Android API level 19 and above.

To install the SDK, in your app-level `build.gradle`, add the following:

```groovy
    dependencies {
        implementation 'com.airwallex:airwallex-core:0.0.1'
    }
```

### Step 2: Confirm Payment Intent
Before confirm the `PaymentIntent`, you must create a `PaymentIntent` on the server and pass it to the client.

> Merchant's server
>1. The API user must first obtain an authentication token by specifying the Client ID and API key generated from the Web Application. This token must be present in the Authorization HTTP header when making other API calls.
And the Client ID and API key can be generated within [Account settings > API keys](https://www.airwallex.com/app/settings/api)
>
>2. Create customer if needed via [`/api/v1/pa/customers/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Customers/_api_v1_pa_customers_create/post)
>
>3. Create a `PaymentIntent` object on your own server via [`/api/v1/pa/payment_intents/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Payment_Intents/_api_v1_pa_payment_intents_create/post) and pass it to your client

Then you can confirm the `PaymentIntent`

1. Initializes an `Airwallex` object, it's the Entry-point of the Airwallex SDK.

```kotlin
    // `clientSecret`&`customerId` are parameters of `PaymentIntent`. `clientSecret` is required, `customerId` is optional.
    val airwallex = Airwallex(clientSecret, customerId)
```

2. Then you can call the `confirmPaymentIntent` method to start confirm the Payment Intent. 
[Wechat Pay](https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_1)
```kotlin
    // `paymentIntentId` is the ID of the `PaymentIntent` and is required.
    airwallex.confirmPaymentIntent(
        paymentIntentId = paymentIntentId,
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                val nextActionData = response.nextAction?.data
                // `nextActionData` contains all the data needed for wechat pay, then you need to send `nextActionData` to wechat sdk.
            }
                
            override fun onFailed(exception: AirwallexException) {
                // Confirm Payment Intent failed
            }  
        }
     )
```
3. After confirm Payment Intent is successful, we will return all the parameters needed for Wechat Pay. You need to call [Wechat Pay SDK](https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_1) to complete the final payment. You can check the [Sample](https://github.com/airwallex/airwallex-payment-android/blob/wechat) for more information.

### Step 3: Retrieve Payment Intent

After Wechat Pay is successful, you can make sure if the Payment Intent is successful by calling the `retrievePaymentIntent` method and checking the `status` of the response.
```kotlin
    airwallex.retrievePaymentIntent(
        paymentIntentId = paymentIntentId,
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                if (response.status == PaymentIntentStatus.SUCCEEDED) {
                   // Payment successful
                } else {
                   // Payment failed
                }
            }
    
            override fun onFailed(exception: AirwallexException) {
                // Payment failed
            }
        })
```

## More information
If you’d like more help, check out our [example app](https://github.com/airwallex/airwallex-payment-android/blob/wechat) on Github that demonstrates the use of the entire payment process. Also, you can read the [Airwallex API](https://www.airwallex.com/docs/api#/Introduction) for more information.