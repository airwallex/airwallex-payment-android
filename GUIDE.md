# Getting started with the Android SDK
This section gives an introduction to the core features of the Airwallex Android SDK and lists all the requirements for adding the SDK in an Android Project. This guide assumes that you have basic understanding of Android development and that you are familiar with Android Studio and Gradle.

## Introduction
With Airwallex Android SDK, you can integrate Airwallex payments into your existing in-person checkout flow or build in-person payments into your application.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to include the Airwallex Android SDK in your Android project.

## Install the SDK
The Android SDK is compatible with apps supporting Android API level 19 and above. Apps can be written using Kotlin or Java 8, but must use AndroidX.

To install the SDK, in your app-level `build.gradle`, add the following:

```groovy
dependencies {
    implementation 'com.airwallex:airwallex-core:0.0.1'
}
```

## Confirm/Retrieve Payment Intent

1. First, you have to create a `PaymentIntent` on your own server

`POST /api/v1/pa/payment_intents/create`.


2. Initializes a `Airwallex` object, `confirmPaymentIntent` this method will confirm the Payment Intent. `customerId` is optional.
```kotlin
    val airwallex = Airwallex(clientSecret)
    airwallexStarter.confirmPaymentIntent(
        paymentIntentId = paymentIntentId,
        customerId = customerId,
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                val nextActionData = response.nextAction?.data
                // Then you need to send `nextActionData` to wechat sdk.
                // You can check the demo on [Sample](https://github.com/airwallex/airwallex-payment-android)
            }
                
            override fun onFailed(exception: AirwallexException) {
                // Confirm Payment Intent failed
            }  
        }
     )
```

3. Finally on the wechat callback, you can call `retrievePaymentIntent` method to determine whether the PaymentIntent was successful
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

## Shipping Flow(optional)
We provide some custom UIs that can be easily integrated into your app

1. Initializes a `AirwallexStarter` object, then call `presentShippingFlow` method, that you can edit shipping addrees.
```kotlin
val airwallexStarter = AirwallexStarter(activity)
airwallexStarter?.presentShippingFlow(shipping)
```

2. You need to call `handlePaymentShippingResult` from your onActivityResult(int, int, Intent)` function.
```kotlin
airwallexStarter?.handlePaymentShippingResult(
    requestCode, resultCode, data, object : AirwallexStarter.PaymentShippingResult {
        override fun onSuccess(shipping: Shipping) {
           // shipping response
        }

        override fun onCancelled() {
            // User cancel edit shipping...
        }
    })
```