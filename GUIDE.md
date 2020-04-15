# Airwallex Android SDK
This section mainly introduces the main process of integrating Airwallex Android SDK. This guide assumes that you are an Android developer and familiar with Android Studio and Gradle.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to include the Airwallex Android SDK in your Android project.

Get started with our integration guide and example project.

## Contents
* [Requirements](#Requirements)
* [Integration](#Integration)
* [Native UI](#NativeUI)
* [Features](#Features)
* [Examples](#Examples)
* [Contributing](#Contributing)

## Requirements
The Airwallex Android SDK is compatible with apps supporting Android API level 19 and above.

## Integration 
Getting started with the Android SDK, please follow below steps:

### Step 1: Install the SDK
To install the SDK, in your app-level `build.gradle`, add the following:

```groovy
    dependencies {
        implementation 'com.airwallex:airwallex-core:0.0.6'
    }
```

### Step 2: Configuration the SDK (optional)
We provide some parameters that can be used to debug the SDK, better to be called in `Application`

```groovy
    Airwallex.initialize(
        AirwallexConfiguration.Builder()
            .enableLogging(true)    // Enable log in sdk, best set to false in release version
            .setBaseUrl(Settings.baseUrl)  // You can change the baseUrl to test other environments
            .build()
    )
```

### Step 3: Confirm Payment Intent
Before confirming the `PaymentIntent`, you must create a `PaymentIntent` on the server and pass it to the client.

> Merchant's server
>1. To begin you will need to obtain an access token to allow you to reach all other API endpoints. Using your unique Client ID and API key (these can be generated within [Account settings > API keys](https://www.airwallex.com/app/settings/api)) you can call the Authentication API endpoint. On success, an access token will be granted.
>
>2. Create customer(optional) allows you to save your customers' details, attach payment methods so you can quickly retrieve the supported payment methods as your customer checks out on your shopping site. [`/api/v1/pa/customers/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Customers/_api_v1_pa_customers_create/post)
>
>3. Finally, you need to create a `PaymentIntent` object on your own server via [`/api/v1/pa/payment_intents/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Payment_Intents/_api_v1_pa_payment_intents_create/post) and pass it to your client

After completing all the steps on the server, the client will get a `PaymentIntent` object from your server, then you can start confirm the `PaymentIntent`

1. Initializes an `Airwallex` object, it's the Entry-point of the Airwallex SDK.

```kotlin
    val airwallex = Airwallex()
```

2. Then you can call the `confirmPaymentIntent` method to start confirming the `PaymentIntent` by ID.
```kotlin
    airwallex.confirmPaymentIntent(
        params = ConfirmPaymentIntentParams.Builder(
            paymentIntentId = paymentIntent.id, // the ID of the `PaymentIntent`, required.
            clientSecret = paymentIntent.clientSecret // the clientSecret of `PaymentIntent`, required.
        )   
            .setCustomerId(paymentIntent.customerId) // the customerId of `PaymentIntent`, optional.
             // If the `paymentMethodType` is `WECHAT`, it's not necessary call `setPaymentMethod` method (default is `WeChat` pay)
             // If the `paymentMethodType` is `CARD`, you should set `PaymentMethodReference`
            .setPaymentMethod(
                PaymentMethodType.CARD,
                PaymentMethodReference(
                    paymentMethod.id,
                    requireNotNull(cvc)
                )
            )
            .build(),
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                // Confirm Payment Intent success
            }
                
            override fun onFailed(exception: AirwallexException) {
                // Confirm Payment Intent failed
            }  
        }
     )
```
3. After successfully confirming the `PaymentIntent`
 - WeChat Pay
 Airwallex will return all the parameters that are needed for WeChat Pay. You need to call [WeChat Pay SDK](https://pay.weixin.qq.com/index.php/public/wechatpay) to complete the final payment.
 Check the [WeChat Pay Sample](https://github.com/airwallex/airwallex-payment-android/blob/master) for more details.
 ```kotlin
     val weChat = response.weChat
     // `weChat` contains all the data needed for WeChat Pay, then you need to send `weChat` to [WeChat Pay SDK](https://pay.weixin.qq.com/index.php/public/wechatpay).
 
     val weChatReq = PayReq()
     weChatReq.appId = weChat.appId
     weChatReq.partnerId = weChat.partnerId
     weChatReq.prepayId = weChat.prepayId
     weChatReq.packageValue = weChat.`package`
     weChatReq.nonceStr = weChat.nonceStr
     weChatReq.timeStamp = weChat.timestamp
     weChatReq.sign = weChat.sign
     
     val weChatApi = WXAPIFactory.createWXAPI(applicationContext, appId)
     weChatApi.sendReq(weChatReq)
 ```
 
 - Credit Card Pay. You can prompt the user the result of confirm PaymentIntent


### Step 4: Retrieve Payment Intent to confirm the charge has succeeded
Since WeChat Pay is a synchronous payment method and the customer has already authorized the payment using the WeChat application. 
After successful payment, the Airwallex server will notify the Merchant, then you can make sure if the `PaymentIntent` is successful by calling the `retrievePaymentIntent` method and checking the `status` of the response.
```kotlin
    airwallex.retrievePaymentIntent(
        params = RetrievePaymentIntentParams(
            paymentIntentId = paymentIntentId, // the ID of the `PaymentIntent`, required.
            clientSecret = clientSecret // the clientSecret of `PaymentIntent`, required.
        ),
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

## NativeUI
We provide native screens to collect payment and shipping details.
You can use these individually, or take all of the prebuilt UI in one flow by following the Integration guide.
```kotlin
    // Create `AirwallexStarter` object
    val airwallexStarter = AirwallexStarter(activity)

    // Override `onActivityResult`, then call `handlePaymentResult`
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airwallexStarter.handlePaymentResult(requestCode, resultCode, data)
    }

    // Call `airwallexStarter.onDestroy()` on `onDestroy`
    override fun onDestroy() {
        airwallexStarter.onDestroy()
        super.onDestroy()
    }
```

- Show Shipping UI
<img src="assets/payment_edit_shipping.jpg" width="300">

```kotlin
    airwallexStarter.presentShippingFlow(shipping,
        object : AirwallexStarter.PaymentShippingListener {
            override fun onSuccess(shipping: Shipping) {
                Log.d(TAG, "Save the shipping success")
            }

            override fun onCancelled() {
                Log.d(TAG, "User cancel edit shipping")
            }
        })
```

- Show Payment Method List UI
<img src="assets/payment_select_payment_method.jpg" width="300">

```kotlin
    airwallexStarter.presentSelectPaymentMethodFlow(paymentIntent,
        object : AirwallexStarter.PaymentMethodListener {
            override fun onSuccess(paymentMethod: PaymentMethod, cvc: String?) {
                Log.d(TAG, "Select PaymentMethod success")
            }

            override fun onCancelled() {
                Log.d(TAG, "User cancel select PaymentMethod")
            }
        })
```

- Show New Card UI
<img src="assets/payment_new_card.jpg" width="300">

```kotlin
    airwallexStarter.presentAddPaymentMethodFlow(paymentIntent,
        object : AirwallexStarter.PaymentMethodListener {
            override fun onSuccess(paymentMethod: PaymentMethod, cvc: String?) {
                Log.d(TAG, "Create PaymentMethod success")
            }

            override fun onCancelled() {
                Log.d(TAG, "User cancel create PaymentMethod")
            }
        })
```

- Show Payment Detail UI
<img src="assets/payment_detail.jpg" width="300">

```kotlin
    airwallexStarter.presentPaymentDetailFlow(paymentIntent, paymentMethod,
        object : AirwallexStarter.PaymentIntentListener {
           override fun onSuccess(paymentIntent: PaymentIntent) {
               Log.d(TAG, "Confirm payment intent success")
            }

           override fun onFailed(error: AirwallexError) {
               Log.d(TAG, "Confirm payment intent failed")
           }
                           
           override fun onCancelled() {
               Log.d(TAG, "User cancel confirm payment intent")
           }
        })
```

- Show Payment Flow
```kotlin
    airwallexStarter.presentPaymentFlow(paymentIntent,
        object : AirwallexStarter.PaymentIntentListener {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                Log.d(TAG, "Confirm payment intent success")
            }

            override fun onFailed(error: AirwallexError) {
                Log.d(TAG, "Confirm payment intent failed")
            }
                
            override fun onCancelled() {
                Log.d(TAG, "User cancel confirm payment intent")
            }
        })
```

## Features

### WeChat Pay & Credit Card Pay
We provide a seamless integration with WeChat Pay & Credit Card Pay.

### Airwallex API
We provide low-level APIs that correspond to objects and methods in the Airwallex API. You can build your own entirely custom UI on top of this layer.

### Payment Native UI
We provide native screens to collect payment and shipping details.

## Examples
To run the example project, you should follow these steps.

* **Step 1:** Clone the repository to your local machine

* **Step 2:** Open Android Studio and import the project by selecting the build.gradle file from the cloned repository

* **Step 3:** Goto [Account settings > API keys](https://www.airwallex.com/app/settings/api) to get `Client ID` and `API key`, then fill in the `strings.xml`

* **Step 4:** Register app on [WeChat Pay](https://pay.weixin.qq.com/index.php/public/wechatpay), then fill `App ID` and `App Signature` in the `strings.xml`
            
* **Step 5:** Run the `sample` project

## Contributing
If you’d like more help, check out our [example app](https://github.com/airwallex/airwallex-payment-android/blob/master) on Github that demonstrates the use of the entire payment process. Also, you can read the [Airwallex API](https://www.airwallex.com/docs/api#/Introduction) for more details.