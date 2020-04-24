# Airwallex Android SDK
This section mainly introduces the main process of integrating Airwallex Android SDK. This guide assumes that you are an Android developer and familiar with Android Studio and Gradle.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to include the Airwallex Android SDK in your Android project.

Get started with our integration guide and example project.

## Contents
* [Requirements](#Requirements)
* [Integration](#Integration)
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
        implementation 'com.airwallex:airwallex-core:1.0.0'
    }
```

### Step 2: Configuration the SDK (optional)
We provide some parameters that can be used to debug the SDK, need to be initialized before calling the airwallex api. It is recommended to be placed in the `Application`

```groovy
    Airwallex.initialize(
        AirwallexConfiguration.Builder()
            .enableLogging(true)    // Enable log in sdk, best set to `false` in release version
            .setBaseUrl(Settings.baseUrl)  // You can change the baseUrl to test other environments
            .build()
    )
```

### Step 3: Confirm Payment Intent
Before confirming the `PaymentIntent`, you must create a `PaymentIntent` on the server side, then return `PaymentIntent` to the client.

> Merchant's server
>1. To begin you will need to obtain an access token to allow you to reach all other API endpoints. Using your unique `Client ID` and `API key` (these can be generated within [Account settings > API keys](https://www.airwallex.com/app/settings/api)) you can call the Authentication API endpoint. On success, an access token will be granted.
>
>2. (optional) Create customer allows you to save your customers' details, attach payment methods so you can quickly retrieve the supported payment methods as your customer checks out on your shopping site. [`/api/v1/pa/customers/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Customers/_api_v1_pa_customers_create/post)
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
            clientSecret = paymentIntent.clientSecret // the Client Secret of `PaymentIntent`, required.
        )   
            .setCustomerId(paymentIntent.customerId) // the customerId of `PaymentIntent`, optional.
            .build(),
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                val weChat = response.weChat
                // `weChat` contains all the data needed for WeChat Pay, then you need to send `weChat` to [WeChat Pay](https://pay.weixin.qq.com/wiki/doc/api/wxpay/pay/In-AppPay/chapter6_2.shtml).
            }
                
            override fun onFailed(exception: AirwallexException) {
                // Confirm Payment Intent failed
            }  
        }
     )
```
3. After successfully confirming the `PaymentIntent`, Airwallex will return all the parameters that are needed for WeChat Pay. You need to call [WeChat Pay](https://pay.weixin.qq.com/wiki/doc/api/wxpay/pay/In-AppPay/chapter6_2.shtml) to complete the final payment.
Check the [WeChat Pay Sample](https://github.com/airwallex/airwallex-payment-android/tree/master) for more details.
```kotlin
    val weChat = response.weChat

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

### Step 4: Retrieve Payment Intent to confirm the charge has succeeded
Since WeChat Pay is a synchronous payment method and the customer has already authorized the payment using the WeChat application. 
After successful payment, the Airwallex server will notify the Merchant, then you can make sure if the `PaymentIntent` is successful by calling the `retrievePaymentIntent` method and checking the `status` of the response.
```kotlin
    airwallex.retrievePaymentIntent(
        params = RetrievePaymentIntentParams(
            paymentIntentId = paymentIntent.id, // the ID of the `PaymentIntent`, required.
            clientSecret = paymentIntent.clientSecret // the Client Secret of `PaymentIntent`, required.
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

## Examples
To run the example project, you should follow these steps.

* **Step 1:** Run the following script to clone the repository to your local machine
`git clone git@github.com:airwallex/airwallex-payment-android.git`

* **Step 2:** Open Android Studio and import the project by selecting the `build.gradle` file from the cloned repository

* **Step 3:** Goto [Airwallex Account settings > API keys](https://www.airwallex.com/app/settings/api), then copy `Client ID` and` API key` to [`strings.xml`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/res/values/strings.xml)

* **Step 4:** Register app on [WeChat Pay](https://pay.weixin.qq.com/index.php/public/wechatpay), then copy `App ID` and `App Signature` to [`strings.xml`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/res/values/strings.xml)
            
* **Step 5:** Run the `sample` module

## Contributing
We welcome contributions of any kind including new features, bug fixes, and documentation improvements. The best way to contribute is by submitting a pull request – we'll do our best to respond to your patch as soon as possible. You can also submit an issue if you find bugs or have any questions.