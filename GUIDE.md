# Getting started with the Android SDK
This section mainly introduces the main process of integrating Airwallex Android SDK. This guide assumes that you are an Android developer and familiar with Android Studio and Gradle.

## Introduction
With Airwallex Android SDK, you can integrate Airwallex Wechat Pay into your application.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to include the Airwallex Android SDK in your Android project.

## Integration 
The whole process mainly includes the following steps:

### Step 1: Install the SDK
The Android SDK is compatible with apps supporting Android API level 19 and above.

To install the SDK, in your app-level `build.gradle`, add the following:

```groovy
    dependencies {
        implementation 'com.airwallex:airwallex-core:0.0.1'
    }
```

### Step 2: Confirm Payment Intent

1. First you must create a `PaymentIntent` object on your own server via `/api/v1/pa/payment_intents/create` and pass it to your client


2. Initializes an `Airwallex` object, it's the Entry-point of the Airwallex SDK.

```kotlin
    // `clientSecret`&`customerId` are parameters of `PaymentIntent`. `clientSecret` is required, `customerId` is optional.
    val airwallex = Airwallex(clientSecret, customerId)
```

3. Then you can call the `confirmPaymentIntent` method to start confirm the Payment Intent. 
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
4. After confirm Payment Intent is successful, we will return all the parameters needed for Wechat Pay. You need to call [Wechat Pay SDK](https://pay.weixin.qq.com/wiki/doc/api/app/app.php?chapter=8_1) to complete the final payment. You can check the [Sample](https://github.com/airwallex/airwallex-payment-android/blob/wechat) for more information.

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