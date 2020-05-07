# Airwallex Android SDK
This section mainly introduces the main process of integrating Airwallex Android SDK. This guide assumes that you are an Android developer and familiar with Android Studio and Gradle.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to include the Airwallex Android SDK in your Android project.

We also provides a powerful, customizable interface.

<p align="center">
<img src="assets/payment_edit_shipping.jpg" width="20%" alt="PaymentShippingActivity" hspace="10">
<img src="assets/payment_select_payment_method.jpg" width="20%" alt="PaymentMethodsActivity" hspace="10">
<img src="assets/payment_new_card.jpg" width="20%" alt="AddPaymentMethodActivity" hspace="10">
<img src="assets/payment_detail.jpg" width="20%" alt="PaymentCheckoutActivity" hspace="10">
</p>

Get started with our integration guide and example project.

## Contents
* [Requirements](#Requirements)
* [Integration](#Integration)
    * [Setup](#setup)
    * [Basic Integration](#basic-integration)
    * [Customize Usage](#customize-usage)
* [Examples](#Examples)
* [Contributing](#Contributing)

## Requirements
The Airwallex Android SDK is compatible with apps supporting Android API level 19 and above.

## Integration 
Getting started with the Android SDK, please follow below steps:

### Setup
- Install the SDK
To install the SDK, in your app-level `build.gradle`, add the following:

```groovy
    dependencies {
        implementation 'com.airwallex:airwallex-core:1.0.0'
    }
```

- Configuration the SDK (optional)
We provide some parameters that can be used to debug the SDK, better to be called in `Application`

```groovy
    Airwallex.initialize(
        AirwallexConfiguration.Builder()
            .enableLogging(true)            // Enable log in sdk, best set to false in release version
            .setBaseUrl(Settings.baseUrl)   // You can change the baseUrl to test other environments
            .build()
    )
```

### Basic Integration

#### Confirm Payment Intent
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

2. Then you can call the `confirmPaymentIntent` method
```kotlin
    val confirmPaymentIntentParams = when (paymentMethod.type) {
        PaymentMethodType.WECHAT -> {
            ConfirmPaymentIntentParams.createWeChatParams(
                paymentIntentId = paymentIntent.id,
                clientSecret = paymentIntent.clientSecret,
                customerId = paymentIntent.customerId
            )
        }
        PaymentMethodType.CARD -> {
            ConfirmPaymentIntentParams.createCardParams(
                paymentIntentId = paymentIntent.id,
                clientSecret = paymentIntent.clientSecret,
                paymentMethodReference = PaymentMethodReference(
                    requireNotNull(paymentMethod.id),
                    requireNotNull(cvc)
                ),
                customerId = paymentIntent.customerId
            )
        }
    }

    airwallex.confirmPaymentIntent(
        params = confirmPaymentIntentParams,
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
Airwallex will return all the parameters that are needed for WeChat Pay. You need to call [WeChat Pay SDK](https://pay.weixin.qq.com/wiki/doc/api/wxpay/pay/In-AppPay/chapter6_2.shtml) to complete the final payment.
Check the [WeChat Pay Sample](https://github.com/airwallex/airwallex-payment-android/tree/master) for more details.
```kotlin
    val weChat = response.weChat
    // `weChat` contains all the data needed for WeChat Pay, then you need to send `weChat` to [WeChat Pay SDK](https://pay.weixin.qq.com/wiki/doc/api/wxpay/pay/In-AppPay/chapter6_2.shtml).

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

#### Retrieve Payment Intent to confirm the charge has succeeded
After successful payment, the Airwallex server will notify the Merchant, then you can make sure if the `PaymentIntent` is successful by calling the `retrievePaymentIntent` method and checking the `status` of the response.
```kotlin
    airwallex.retrievePaymentIntent(
        params = RetrievePaymentIntentParams(
            paymentIntentId = paymentIntentId,  // the ID of the `PaymentIntent`, required.
            clientSecret = clientSecret         // the clientSecret of `PaymentIntent`, required.
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

### Customize Usage
We provide native screens to facilitate the integration of payment functions.
You can use these individually, or take all of the prebuilt UI in one flow by following the Integration guide.

- Initialization the `AirwallexStarter`
```kotlin
    // Create `AirwallexStarter` object
    val airwallexStarter = AirwallexStarter(activity)

    // Override `onActivityResult`, then call `handlePaymentResult`
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        airwallexStarter.onActivityResult(requestCode, resultCode, data)
    }

    // Call `airwallexStarter.onDestroy()` on `onDestroy`
    override fun onDestroy() {
        airwallexStarter.onDestroy()
        super.onDestroy()
    }
```

- Customize the usage of shipping info, shipping parameter is optional. After successfully saving, it will return a shipping object
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

- Customize the usage of select one of payment methods, you need to pass in a `paymentIntent` object. It will display all the saved payment methods of the current customer, you can choose any one to pay
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

- Customize the usage of card creation you can enter a credit card number, expiration time and cvc to create a payment method. You need to pass in a `paymentIntent` object.
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

- Customize the usage of payment detail. You need to pass in a `paymentIntent` object and a `paymentMethod` object. It will display the current payment amount has been paid, encapsulated the specific operation of payment, will return the PaymentIntent or AirwallexError through the callback method
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

- Show Payment Flow, need to pass in a `paymentIntent` object. You can complete the entire payment process by calling this method, will return the PaymentIntent or AirwallexError through the callback method
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

## Examples
To run the example project, you should follow these steps.

* **Step 1:** Run the following script to clone the repository to your local machine
`git clone git@github.com:airwallex/airwallex-payment-android.git`

* **Step 2:** Open Android Studio and import the project by selecting the `build.gradle` file from the cloned repository

* **Step 3:** Goto [Airwallex Account settings > API keys](https://www.airwallex.com/app/settings/api), then copy `Client ID` and` API key` to [`strings.xml`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/res/values/strings.xml)

* **Step 4:** Register app on [WeChat Pay](https://pay.weixin.qq.com/index.php/public/wechatpay), then copy `App ID` and `App Signature` to [`strings.xml`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/res/values/strings.xml)
            
* **Step 5:** Run the `sample` project

## Contributing
We welcome contributions of any kind including new features, bug fixes, and documentation improvements. The best way to contribute is by submitting a pull request – we'll do our best to respond to your patch as soon as possible. You can also submit an issue if you find bugs or have any questions.
