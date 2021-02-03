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
* [Test Card Numbers](#test-card-numbers)
* [Custom Theme](#custom-theme)
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
        implementation 'com.airwallex:airwallex-core:2.0.2'
    }
```

Additionally, add the following Maven repository and (non-sensitive) credentials to your app-level `build.gralde`:
```groovy
repositories {
    maven {
        url "https://cardinalcommerce.bintray.com/android"
        credentials {
            username 'qiao.zhao@cardinalcommerce'
            password '99796fb351b999db8dced5b3f6ba6015efc862e7'
        }
    }
}
```

- Configuration the SDK (optional)
We provide some parameters that can be used to debug the SDK, better to be called in `Application`

```groovy
    Airwallex.initialize(
        AirwallexConfiguration.Builder()
            .enableLogging(true)                // Enable log in sdk, best set to false in release version
            .setEnvironment(Environment.DEMO)   // You can change the environment of Airwallex
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

*Important* 
If you need to support one of Alipaycn Alipayhk dana gcash kakaopay tng, you need to pass in `returnUrl` when creating `PaymentIntent`, so that you can jump back to the merchant app after successful payment
```kotlin
    api.createPaymentIntent(
        mutableMapOf(
            
            // The HTTP request method that you should use. After the shopper completes the payment, they will be redirected back to your returnURL using the same method.
            "return_url" to "airwallexcheckout://$packageName"
        )
    )
```
```xml
    <activity android:name="...">
        <intent-filter>
            <action android:name="android.intent.action.VIEW" />

            <category android:name="android.intent.category.DEFAULT" />
            <category android:name="android.intent.category.BROWSABLE" />

            <data
                android:host="${applicationId}"
                android:scheme="airwallexcheckout" />
        </intent-filter>
    </activity>
```

After completing all the steps on the server, the client will get a `PaymentIntent` object from your server, then you can start confirm the `PaymentIntent`

1. Initializes an `Airwallex` object, it's the Entry-point of the Airwallex SDK.

```kotlin
    val airwallex = Airwallex(this)
```

2. Then you can call the `confirmPaymentIntent` method
```kotlin
    val listener = object : Airwallex.PaymentListener<PaymentIntent> {
        override fun onSuccess(response: PaymentIntent) {
            // Confirm Payment Intent success
        }

        override fun onFailed(exception: Exception) {
            // Confirm Payment Intent failed
        }
    }
    val params = when (requireNotNull(paymentMethod.type)) {
        PaymentMethodType.WECHAT -> {
            ConfirmPaymentIntentParams.createWeChatParams(
                paymentIntentId = paymentIntent.id, // Required
                clientSecret = requireNotNull(paymentIntent.clientSecret), // Required
                customerId = paymentIntent.customerId // Optional
            )
        }
        PaymentMethodType.ALIPAY_CN -> {
            ConfirmPaymentIntentParams.createAlipayParams(
                paymentIntentId = paymentIntent.id, // Required
                clientSecret = requireNotNull(paymentIntent.clientSecret), // Required
                customerId = paymentIntent.customerId // Optional
            )
        }
        PaymentMethodType.ALIPAY_HK -> {
            ConfirmPaymentIntentParams.createAlipayHKParams(
                paymentIntentId = paymentIntent.id, // Required
                clientSecret = requireNotNull(paymentIntent.clientSecret), // Required
                customerId = paymentIntent.customerId // Optional
            )
        }
        PaymentMethodType.DANA -> {
            ConfirmPaymentIntentParams.createDanaParams(
                paymentIntentId = paymentIntent.id, // Required
                clientSecret = requireNotNull(paymentIntent.clientSecret), // Required
                customerId = paymentIntent.customerId // Optional
            )
        }
        PaymentMethodType.GCASH -> {
            ConfirmPaymentIntentParams.createGCashParams(
                paymentIntentId = paymentIntent.id, // Required
                clientSecret = requireNotNull(paymentIntent.clientSecret), // Required
                customerId = paymentIntent.customerId // Optional
            )
        }
        PaymentMethodType.KAKAOPAY -> {
            ConfirmPaymentIntentParams.createKakaoParams(
                paymentIntentId = paymentIntent.id, // Required
                clientSecret = requireNotNull(paymentIntent.clientSecret), // Required
                customerId = paymentIntent.customerId // Optional
            )
        }
        PaymentMethodType.TNG -> {
            ConfirmPaymentIntentParams.createTngParams(
                paymentIntentId = paymentIntent.id, // Required
                clientSecret = requireNotNull(paymentIntent.clientSecret), // Required
                customerId = paymentIntent.customerId // Optional
            )
        }
        PaymentMethodType.CARD -> {
            ConfirmPaymentIntentParams.createCardParams(
                paymentIntentId = paymentIntent.id, // Required
                clientSecret = requireNotNull(paymentIntent.clientSecret), // Required
                paymentMethodId = requireNotNull(paymentMethod.id), // Required
                cvc = requireNotNull(cvc), // Required
                customerId = paymentIntent.customerId // Optional
            )
        }
    }
    airwallex.confirmPaymentIntent(params, listener)
```

```kotlin
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        
        // You must call this method on `onActivityResult`
        airwallex.handlePaymentData(requestCode, resultCode, data)
    }
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

- Alipaycn Alipayhk dana gcash kakaopay tng

After the Confirm `PaymentIntent` is successful, you need to call `handleAction` to redirect to the payment screen. After the payment is completed, it will be redirected to the merchant app. Then you can check the order status through `retrievePaymentIntent

```kotlin
    try {
        airwallex.handleAction(paymentIntent.nextAction)
    } catch (e: RedirectException) {
        showPaymentError(e.localizedMessage)
    }
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
    
            override fun onFailed(exception: Exception) {
                
            }
        })
```

### Customize Usage
We provide native screens to facilitate the integration of payment functions.
You can use these individually, or take all of the prebuilt UI in one flow by following the Integration guide.

- Customize the usage of shipping info, shipping parameter is optional. After successfully saving, it will return a shipping object
```kotlin
    airwallex.presentShippingFlow(shipping,
        object : Airwallex.PaymentShippingListener {
            override fun onSuccess(shipping: Shipping) {
                Log.d(TAG, "Save the shipping success")
            }

            override fun onCancelled() {
                Log.d(TAG, "User cancel edit shipping")
            }
        })
```

- Customize the usage of select one of payment methods, you need to pass in a `PaymentIntent` and `ClientSecretProvider` object. It will display all the saved payment methods of the current customer, you can choose any one to pay
```kotlin
    private val clientSecretProvider by lazy {
        ExampleClientSecretProvider()
    }
    airwallex.presentSelectPaymentMethodFlow(paymentIntent, clientSecretProvider,
        object : Airwallex.PaymentMethodListener {
            override fun onSuccess(paymentMethod: PaymentMethod, cvc: String?) {
                Log.d(TAG, "Select PaymentMethod success")
            }

            override fun onCancelled() {
                Log.d(TAG, "User cancel select PaymentMethod")
            }
        })
```

- Customize the usage of card creation you can enter a credit card number, expiration time and cvc to create a payment method. You need to pass in a `paymentIntent` and `ClientSecretProvider` object.
```kotlin
    private val clientSecretProvider by lazy {
        ExampleClientSecretProvider()
    }
    airwallex.presentAddPaymentMethodFlow(paymentIntent,
        object : Airwallex.AddPaymentMethodListener {
            override fun onSuccess(paymentMethod: PaymentMethod, cvc: String) {
                Log.d(TAG, "Create PaymentMethod success")
            }

            override fun onCancelled() {
                Log.d(TAG, "User cancel create PaymentMethod")
            }
        })
```

- Customize the usage of payment detail. You need to pass in a `PaymentIntent` object and a `PaymentMethod` object. It will display the current payment amount has been paid, encapsulated the specific operation of payment, will return the `PaymentIntent` or `Exception` through the callback method
```kotlin
    airwallex.presentPaymentDetailFlow(paymentIntent, paymentMethod,
        object : Airwallex.PaymentIntentListener {
           override fun onSuccess(paymentIntent: PaymentIntent) {
               Log.d(TAG, "Confirm payment intent success")
            }

           override fun onFailed(exception: Exception) {
               Log.d(TAG, "Confirm payment intent failed")
           }
                           
           override fun onCancelled() {
               Log.d(TAG, "User cancel confirm payment intent")
           }
        })
```

- Show Payment Flow, need to pass in a `PaymentIntent` object. You can complete the entire payment process by calling this method, will return the `PaymentIntent` or `Exception` through the callback method
```kotlin
    private val clientSecretProvider by lazy {
        ExampleClientSecretProvider()
    }
    airwallex.presentPaymentFlow(paymentIntent, clientSecretProvider,
        object : Airwallex.PaymentIntentListener {
            override fun onSuccess(paymentIntent: PaymentIntent) {
                Log.d(TAG, "Confirm payment intent success")
            }

            override fun onFailed(exception: Exception) {
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

* **Step 3:** Goto [Airwallex Account settings > API keys](https://www.airwallex.com/app/settings/api), then copy `Client ID` and` API key` to [`Settings.kt`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/java/com/airwallex/paymentacceptance/Settings.kt)
```
    private const val AUTH_URL = "put your auth url here"
    private const val BASE_URL = "put your base url here"
    private const val API_KEY = "put your api key here"
    private const val CLIENT_ID = "put your client id here"
```

* **Step 4:** Register app on [WeChat Pay](https://pay.weixin.qq.com/index.php/public/wechatpay), then copy `App ID` and `App Signature` to [`Settings.kt`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/java/com/airwallex/paymentacceptance/Settings.kt)
```
    private const val WECHAT_APP_ID = "put your WeChat app id here"
    private const val WECHAT_APP_SIGNATURE = "put your WeChat app signature here"
```

* **Step 5:** Run the `sample` project

## Test Card Numbers
- 4242 4242 4242 4242
- 2223 0000 1018 1375 Expire: 12/2022 CVC: 650 (3DS 1.0)
- 4012 0003 0000 1003 Month: 12 (3DS 1.0)
- 4012 0003 0000 1003 Month: 10 (3DS 2.0)

## Custom Theme
You can overwrite these color values in your app. https://developer.android.com/guide/topics/ui/look-and-feel/themes#CustomizeTheme
```
    <!--   a secondary color for controls like checkboxes and text fields -->
    <color name="airwallex_color_accent">@color/color_accent</color>

    <!--   color for the app bar and other primary UI elements -->
    <color name="airwallex_color_primary">@color/color_primary</color>

    <!--   a darker variant of the primary color, used for
           the status bar (on Android 5.0+) and contextual app bars -->
    <color name="airwallex_color_primary_dark">@color/color_primary_dark</color>
```

## Contributing
We welcome contributions of any kind including new features, bug fixes, and documentation improvements. The best way to contribute is by submitting a pull request – we'll do our best to respond to your patch as soon as possible. You can also submit an issue if you find bugs or have any questions.
