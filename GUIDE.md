# Getting started with the Android SDK
This section mainly introduces the main process of integrating Airwallex Android SDK. This guide assumes that you are an Android developer and familiar with Android Studio and Gradle.

## Introduction
With Airwallex Android SDK, you can integrate Airwallex WeChat Pay into your application.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to include the Airwallex Android SDK in your Android project.

## Integration 
Getting started with the Android SDK, please follow below steps:

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
>1. To begin you will need to obtain an access token to allow you to reach all other API endpoints. Using your unique Client ID and API key (these can be generated within [Account settings > API keys](https://www.airwallex.com/app/settings/api)) you can call the Authentication API endpoint. On success, an access token will be granted.
>
>2. Create customer(optional) allows you to save your customers' details, attach payment methods so you can quickly retrieve the supported payment methods as your customer checks out on your shopping site. [`/api/v1/pa/customers/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Customers/_api_v1_pa_customers_create/post)
>
>3. Finally, you need to create a `PaymentIntent` object on your own server via [`/api/v1/pa/payment_intents/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Payment_Intents/_api_v1_pa_payment_intents_create/post) and pass it to your client

After completing all the steps on the server, the client will get a `PaymentIntent` object from your server, then you can start confirm the `PaymentIntent`

1. Initializes an `Airwallex` object, it's the Entry-point of the Airwallex SDK.

```kotlin
    // `clientSecret`&`customerId` are parameters of `PaymentIntent`. 
    // `clientSecret` is required, `customerId` is optional.
    val airwallex = Airwallex(clientSecret, customerId)
```

2. Then you can call the `confirmPaymentIntent` method to start confirm the `PaymentIntent` by ID.
```kotlin
    // `paymentIntentId` is the ID of the `PaymentIntent` and is required.
    airwallex.confirmPaymentIntent(
        paymentIntentId = paymentIntentId,
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                val nextActionData = response.nextAction?.data
                // `nextActionData` contains all the data needed for WeChat Pay, then you need to send `nextActionData` to [WeChat Pay SDK](https://pay.weixin.qq.com/index.php/public/wechatpay).
            }
                
            override fun onFailed(exception: AirwallexException) {
                // Confirm Payment Intent failed
            }  
        }
     )
```
3. After successful confirm the `PaymentIntent`, Airwallex will return all the parameters that needed for WeChat Pay. You need to call [WeChat Pay SDK](https://pay.weixin.qq.com/index.php/public/wechatpay) to complete the final payment.
Check the [WeChat Pay Sample](https://github.com/airwallex/airwallex-payment-android/blob/master) for more details.

### Step 3: Retrieve Payment Intent to confirm the charge has succeeded
Since WeChat Pay is a synchronous payment method and the customer has already authorized the payment using the WeChat application. 
After successful payment, the Airwallex server will notify the Merchant, then you can make sure if the `PaymentIntent` is successful by calling the `retrievePaymentIntent` method and checking the `status` of the response.
```kotlin
    airwallex.retrievePaymentIntent(
        paymentIntentId = paymentIntentId,
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                if (response.status == PaymentIntentStatus.SUCCEEDED) {
                   // Payment successful
                }
            }
    
            override fun onFailed(exception: AirwallexException) {
                
            }
        })
```

## More information
If you’d like more help, check out our [example app](https://github.com/airwallex/airwallex-payment-android/blob/master) on Github that demonstrates the use of the entire payment process. Also, you can read the [Airwallex API](https://www.airwallex.com/docs/api#/Introduction) for more details.