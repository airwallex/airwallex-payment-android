# Getting started with the Android SDK (Wechat Pay Only)
This section mainly introduces the main process of integrating Airwallex Android SDK. This guide assumes that you are an Android developer and familiar with Android Studio and Gradle

## Introduction
With Airwallex Android SDK, you can integrate Airwallex Wechat payment into your application.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to include the Airwallex Android SDK in your Android project.

## Install the SDK
The Android SDK is compatible with apps supporting Android API level 19 and above.

To install the SDK, in your app-level `build.gradle`, add the following:

```groovy
dependencies {
    implementation 'com.airwallex:airwallex-core:0.0.1'
}
```

## Confirm Payment Intent

1. First you must create a `PaymentIntent` object on your own server via `/api/v1/pa/payment_intents/create` and pass it to your client


2. Initializes an `Airwallex` object, `clientSecret`&`customerId` are parameters of `PaymentIntent`. `clientSecret` is required, `customerId` is optional.
    
```kotlin
    val airwallex = Airwallex(clientSecret, customerId)
```

3. Call the `confirmPaymentIntent` method to start confirm the Payment Intent. `paymentIntentId` is the ID of the `PaymentIntent` and is required.
```kotlin
    airwallex.confirmPaymentIntent(
        paymentIntentId = paymentIntentId,
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                val nextActionData = response.nextAction?.data
                // `nextActionData` contains all the data needed for wechat pay, then you need to send `nextActionData` to wechat sdk.
                // You can check the demo on [Sample](https://github.com/airwallex/airwallex-payment-android) for more informations
            }
                
            override fun onFailed(exception: AirwallexException) {
                // Confirm Payment Intent failed
            }  
        }
     )
```

## Retrieve Payment Intent

After wechat payment is successful, You can check whether the payment is successful by calling the `retrievePaymentIntent` method and checking the `status` of the payment intent.
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