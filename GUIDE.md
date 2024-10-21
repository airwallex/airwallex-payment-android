# Airwallex Android SDK
Airwallex Android SDK is a flexible tool that enables you to integrate payment methods into your Android App. It also includes a prebuilt UI that provides you the flexibility to choose to use any part of it, while replacing the rest with your own UI.

This section will guide you through the process of integrating Airwallex Android SDK. We assume you are an Android developer and familiar with Android Studio and Gradle.

To accept online payments with Airwallex Android SDK, please complete preparation work first and choose the integration option according to your need. 

*Preparation*
1. [Before you start](#before-you-start)

*Integration options*
1. [Airwallex Native UI integration](#airwallex-native-ui-integration)You can choose to use this SDK with our prebuilt UI page, this is **recommended usage**. 
2. [Low-level API Integration](#low-level-api-integration)You can build your own custom UI and use our low-level APIs.

Our demo application is available open source on [Github](https://github.com/airwallex/airwallex-payment-android) and it will help you to better understand how to integrate Airwallex Android SDK in your Android App.

## Contents
* [Overview](#Overview)
    * [Airwallex API](#airwallex-api)
    * [Airwallex Native UI](#airwallex-native-ui)
* [Before you start](#before-you-start)
* [Airwallex Native UI integration](#airwallex-native-ui-integration)
    * [Set up SDK](#set-up-sdk)
    * [Configuration and preparation](#configuration-and-preparation) 
    * [Edit Shipping Info](#edit-shipping-info)
    * [Use the entire Native UI in one flow](#use-the-entire-native-ui-in-one-flow)
    * [Set up Google Pay](#set-up-google-pay)
    * [Custom Theme](#custom-theme)
* [Low-level API Integration](#low-level-api-integration)
    * [Step 1: Set up SDK](#step-1-set-up-sdk)
    * [Step 2: Configuration and preparation](#step-2-configuration-and-preparation)
    * [Step 3: Create AirwallexSession and Airwallex object](#step-3-create-airwallexsession-and-airwallex-object)
    * [Confirm payment with card and billing details](#confirm-payment-with-card-and-billing-details)
    * [Confirm payment with Consent ID](#confirm-payment-with-consent-id)
    * [Confirm payment with PaymentConsent](#confirm-payment-with-paymentconsent)
    * [Retrieve the list of payment methods](#retrieve-the-list-of-payment-methods)
    * [Retrieve the list of saved cards](#retrieve-the-list-of-saved-cards)
    * [Launch payment via Google Pay](#launch-payment-via-google-pay)
    * [Pay by redirection](#pay-by-redirection)
* [SDK Example](#sdk-example)
* [Test Card Numbers](#test-card-numbers)
* [Contributing](#Contributing)

## Overview

### Airwallex API

Airwallex Android SDK is a flexible tool that enables you to integrate payment methods into your Android App. 

Note: The Airwallex Android SDK is compatible with apps supporting Android API level 21 and above and SDK file size is 3188.04KB approximately

Payment methods supported: 
- Cards: [`Visa, Mastercard`](#cards). If you want to integrate Airwallex API without our Native UI for card payments, then your website is required to be PCI-DSS compliant. 
- E-Wallets: [`Alipay`](#alipay), [`AlipayHK`](#alipayhk), [`DANA`](#dana), [`GCash`](#gcash), [`Kakao Pay`](#kakao-pay), [`Touch ‘n Go`](#touch-n-go), [`WeChat Pay`](#wechat-pay)

### Airwallex Native UI
Airwallex Native UI is a prebuilt UI which enables you to customize the UI color and fit your App theme. You can use these components separately, or pack our prebuilt UI into one flow to present your payment.
|#|Native UI|Picture|
|---|---|----
|1|[`Edit shipping info page`](#edit-shipping-info)<br/>This page contains a detailed shipping form for shopper to fill in, after the shopper successfully saved the form, the shipping info object will be returned|<p align="center"><img src="assets/payment_edit_shipping.jpg" width="90%" alt="PaymentShippingActivity" hspace="10"></p>
|2|[`Select payment method page`](#selecting-payment-method-page)<br/>This page will display all the available payment methods to shopper, the shopper can choose any one of them to pay|<p align="center"><img src="assets/payment_select_payment_method.jpg" width="90%" alt="PaymentMethodsActivity" hspace="10"></p>
|3|[`Input card information module`](#input-card-information-module)<br/>This module contains card number, expiration date and cvv.|<p align="center"><img src="assets/payment_new_card.jpg" width="90%" alt="AddPaymentMethodActivity" hspace="10"></p>
|4|[`Confirm payment intent page`](#confirm-payment-intent-page)<br/>You need to pass in a PaymentIntent object and a PaymentMethod object. It will display the current selected payment amount, encapsulate the specific operation of payment, and return the PaymentIntent or Exception through the callback method|<p align="center"><img src="assets/payment_detail.jpg" width="90%" alt="PaymentCheckoutActivity" hspace="10"></p>

## Before you start
We offer two methods for integrating Airwallex services. The first method provides pre-built user interfaces that you can directly invoke in your project. The second method provides low-level APIs, which require you to build your own user interfaces. You can choose the integration method based on your needs.

## Airwallex Native UI integration
We provide native screens to facilitate the integration of payment functions.

### Set up SDK
The Airwallex Android SDK is compatible with apps supporting Android API level 21 and above.

- Install the SDK
  The Components are available through [Maven Central](https://repo1.maven.org/maven2/io/github/airwallex/), you only need to add the Gradle dependency.

To install the SDK, in your app-level `build.gradle`, add the following:

```groovy
    dependencies {
        // It's required
        implementation 'io.github.airwallex:payment:6.0.3'
        
        // Select the payment method you want to support.
        implementation 'io.github.airwallex:payment-card:6.0.3'
        implementation 'io.github.airwallex:payment-redirect:6.0.3'
        implementation 'io.github.airwallex:payment-wechat:6.0.3'
        implementation 'io.github.airwallex:payment-googlepay:6.0.3'
    }
```

### Configuration and preparation
After setting up the SDK, you are required to config your SDK with some parameters. Before using Airwallex SDK to confirm payment intents and complete the payments, you shall create payment intents in your own server, to make sure you maintain information in your own system
#### Configuration the SDK

We provide some parameters that can be used to debug the SDK, you can call it in Application
```kotlin
   AirwallexStarter.initialize(
      application,
      AirwallexConfiguration.Builder()
          .enableLogging(true) // Enable log in sdk, best set to false in release version
          .saveLogToLocal(false)// Save the Airwallex logs locally. If you have your own saving strategy, please set this to false.
          .setEnvironment(environment)
          .setSupportComponentProviders(
            listOf(
              CardComponent.PROVIDER,
              WeChatComponent.PROVIDER,
              RedirectComponent.PROVIDER,
              GooglePayComponent.PROVIDER
            )
          )
          .build()
   )
```

#### Create Payment Intent (On the Merchant’s server)

Before confirming the `PaymentIntent`, You must create a `PaymentIntent` on the server and pass it to the client.

> Follow these steps to create a PaymentIntent on the Merchant’s server
>1. To begin you will need to obtain an access token to allow you to reach Airwallex API endpoints. Using your unique Client ID and API key (these can be generated within [Account settings > API keys](https://www.airwallex.com/app/settings/api)) you can call the Authentication API endpoint. On success, an access token will be granted.
>
>2. Create customer(optional) allows you to save your customers’ details, attach payment methods so you can quickly retrieve the supported payment methods as your customer checks out on your shopping site. [`/api/v1/pa/customers/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Customers/_api_v1_pa_customers_create/post)
>
>3. Finally, you need to create a `PaymentIntent` object on the Merchant’s server via [`/api/v1/pa/payment_intents/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Payment_Intents/_api_v1_pa_payment_intents_create/post) and pass it to the client.
>
>4. In the response of each payment intent, you will be returned with client_secret, which you will need to store for later uses.

After creating the payment intent, you can use Airwallex SDK to confirm payment intent and enable the shopper to complete the payment with selected payment methods

### Edit shipping info
Use `presentShippingFlow` to allow users to provide a shipping address as well as select a shipping method. `shipping` parameter is optional.
```kotlin
    AirwallexStarter.presentShippingFlow(
       activity = activity,
       shipping = shipping,
       shippingResultListener = object : Airwallex.ShippingResultListener {
           override fun onCompleted(status: AirwallexShippingStatus) {
               
           }
       }
    )
```

### Use the entire Native UI in one flow

- For the redirect payment method. You need to configure scheme url on `AndroidManifest.xml`
```
    <intent-filter>
        ...
        <data
            android:host="${applicationId}"
            android:scheme="airwallexcheckout" />
    </intent-filter>
```
- Before invoking Airwallex's UI components, you need to create an AirwallexSession object
#### Create an AirwallexSession object
```kotlin
    private fun buildAirwallexPaymentSession(googlePayOptions: GooglePayOptions? = null, paymentIntent: PaymentIntent) = 
        AirwallexPaymentSession.Builder(
          paymentIntent = paymentIntent,
          countryCode = Settings.countryCode,
          googlePayOptions = googlePayOptions
        )
            .setRequireBillingInformation(true)
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            .setReturnUrl(Settings.returnUrl)
            .setAutoCapture(autoCapture)
            .setHidePaymentConsents(false)
            .setPaymentMethods(listOf())
            .build()
```
```kotlin
    private fun buildAirwallexRecurringSession(customerId: String, clientSecret: String) = 
        AirwallexRecurringSession.Builder(
          customerId = customerId,
          clientSecret = clientSecret,
          currency = Settings.currency,
          amount = BigDecimal.valueOf(Settings.price.toDouble()),
          nextTriggerBy = nextTriggerBy,
          countryCode = Settings.countryCode
        )
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            .setShipping(shipping)
            .setRequireCvc(Settings.requiresCVC.toBoolean())
            .setMerchantTriggerReason(if (nextTriggerBy == PaymentConsent.NextTriggeredBy.MERCHANT) PaymentConsent.MerchantTriggerReason.SCHEDULED else PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
            .setReturnUrl(Settings.returnUrl)
            .setPaymentMethods(listOf())
            .build()
```
```kotlin
    private fun buildAirwallexRecurringWithIntentSession(paymentIntent: PaymentIntent) = 
        AirwallexRecurringWithIntentSession.Builder(
          paymentIntent = paymentIntent, 
          customerId = requireNotNull(paymentIntent.customerId, { "CustomerId is required" }), 
          nextTriggerBy = nextTriggerBy,
          countryCode = Settings.countryCode
        )
            .setRequireEmail(Settings.requiresEmail.toBoolean())
            .setRequireCvc(Settings.requiresCVC.toBoolean())
            .setMerchantTriggerReason(if (nextTriggerBy == PaymentConsent.NextTriggeredBy.MERCHANT) PaymentConsent.MerchantTriggerReason.SCHEDULED else PaymentConsent.MerchantTriggerReason.UNSCHEDULED)
            .setReturnUrl(Settings.returnUrl)
            .setAutoCapture(autoCapture)
            .setPaymentMethods(listOf())
            .build()
```
- Use `presentEntirePaymentFlow` to launch the payment list page and complete the entire payment process
```kotlin
   AirwallexStarter.presentEntirePaymentFlow(
       activity = activity,
       session = session,
       paymentResultListener = object : Airwallex.PaymentResultListener { 
           override fun onCompleted(status: AirwallexPaymentStatus) {
              //Handle the payment result
           }
       }
   )
```
- Use `presentCardPaymentFlow` to launch the card payment page and complete the entire payment process.
```kotlin
   AirwallexStarter.presentCardPaymentFlow(
       activity = activity,
       session = session,
       paymentResultListener = object : Airwallex.PaymentResultListener { 
           override fun onCompleted(status: AirwallexPaymentStatus) {
               //Handle the payment result
           }
       }
   )
```
- The card payment page supports dialog mode, and you can also directly create an `AirwallexAddPaymentDialog`
```kotlin
   val dialog = AirwallexAddPaymentDialog(
       activity = activity,
       session = session,
       paymentResultListener = object : Airwallex.PaymentResultListener {
           override fun onCompleted(status: AirwallexPaymentStatus) {
               //Handle the payment result
           }
       }
   )
   dialog.show()
```

- To obtain the payment result, you can use the `retrievePaymentIntent` method and check the latest status. Then you can prompt the shopper with the result.
```kotlin
    airwallex.retrievePaymentIntent(
        params = RetrievePaymentIntentParams(
            // the ID of the `PaymentIntent`, required.
            paymentIntentId = paymentIntentId,
            // the clientSecret of `PaymentIntent`, required.
            clientSecret = clientSecret
        ),
        listener = object : Airwallex.PaymentListener<PaymentIntent> {
            override fun onSuccess(response: PaymentIntent) {
                onComplete.invoke(response)
            }

            override fun onFailed(exception: AirwallexException) {
                Log.e(TAG, "Retrieve PaymentIntent failed", exception)
            }
        }
    )
```

### Set up Google Pay
The Airwallex Android SDK allows merchants to provide Google Pay as a payment method to their customers by the following steps:
- Make sure Google Pay is enabled on your Airwallex account.
- Include the Google Pay module when installing the SDK as per [Set up SDK](#set-up-sdk).
- You can customize the Google Pay options to restrict as well as provide extra context. For more information, please refer to `GooglePayOptions` class.
```kotlin
val googlePayOptions = GooglePayOptions(
    allowedCardAuthMethods = listOf("CRYPTOGRAM_3DS"),
    billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
    shippingAddressParameters = ShippingAddressParameters(listOf("AU", "CN"), true)
)
val paymentSession = AirwallexPaymentSession.Builder(
    paymentIntent = ...,
    countryCode = ...,
    googlePayOptions = googlePayOptions
)
```
- We currently only support Visa and MasterCard for Google Pay, customers will only be able to select the cards of these payment networks during Google Pay.
> Please note that our Google Pay module only supports `AirwallexPaymentSession` at the moment. We'll add support for recurring payment sessions in the future.

### Custom Theme
You can overwrite these color values in your app. https://developer.android.com/guide/topics/ui/look-and-feel/themes#CustomizeTheme
```
    <color name="airwallex_tint_color">@color/airwallex_color_red</color>
```

## Low-level API Integration
You can build your own entirely custom UI on top of our low-level APIs.

### Step 1: Set up SDK
The Airwallex Android SDK is compatible with apps supporting Android API level 21 and above.

- Install the SDK
  The Components are available through [Maven Central](https://repo1.maven.org/maven2/io/github/airwallex/), you only need to add the Gradle dependency.

To install the SDK, in your app-level `build.gradle`, add the following:

```groovy
    dependencies {
        // It's required
        implementation 'io.github.airwallex:payment-components-core:6.0.3'

       // Select the payment method you want to support, ignore the components you don't need.
       implementation 'io.github.airwallex:payment-card:6.0.3'//only support card
       implementation 'io.github.airwallex:payment-googlepay:6.0.3'//only support google pay
       implementation 'io.github.airwallex:payment-redirect:6.0.3'//only support redirect
    }
```

### Step 2: Configuration and preparation
After setting up the SDK, you are required to config your SDK with some parameters. Before using Airwallex SDK to confirm payment intents and complete the payments, you shall create payment intents in your own server, to make sure you maintain information in your own system
#### Configuration the SDK

We provide some parameters that can be used to debug the SDK, you can call it in Application
```kotlin
     Airwallex.initialize(
         this,
         AirwallexConfiguration.Builder()
             .enableLogging(true) // Enable log in sdk, best set to false in release version
             .saveLogToLocal(false)// Save the Airwallex logs locally. If you have your own saving strategy, please set this to false.
             .setEnvironment(environment)
             .setSupportComponentProviders(
               listOf(
                 CardComponent.PROVIDER, 
                 WeChatComponent.PROVIDER,
                 RedirectComponent.PROVIDER,
                 GooglePayComponent.PROVIDER
               )
             )
             .build()
     )
```

#### Create Payment Intent
[Create Payment Intent (On the Merchant’s server)](#create-payment-intent-on-the-merchants-server)

### Step 3: Create AirwallexSession and Airwallex object
[Create an AirwallexSession object](#create-an-airwallexSession-object)
#### Create an Airwallex object
```kotlin
val airwallex = Airwallex(activity)
```
### Confirm payment with card and billing details
```kotlin
// Confirm intent with card and billing
airwallex.confirmPaymentIntent(
    session = session,
    card = PaymentMethod.Card.Builder()
        .setNumber("4012000300000021")
        .setName("John Citizen")
        .setExpiryMonth("12")
        .setExpiryYear("2029")
        .setCvc("737")
        .build(),
    billing = null,
    saveCard = false,//set saveCard to true to save the card information while making the payment.
    listener = object : Airwallex.PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // You can handle different payment statuses and perform UI action respectively here
        }
    }
)
```
### Confirm payment with Consent ID
```kotlin
     airwallex.confirmPaymentIntent(
        session = session,
        paymentConsentId = "cst_xxxxxxxxxx",
        listener = object : Airwallex.PaymentResultListener { 
            override fun onCompleted(status: AirwallexPaymentStatus) {
              // You can handle different payment statuses and perform UI action respectively here 
            } 
        }
     )
```
### Confirm payment with PaymentConsent
```kotlin
     airwallex.confirmPaymentIntent(
        session = session as AirwallexPaymentSession,
        paymentConsent = paymentConsent,
        listener = object : Airwallex.PaymentResultListener {
            override fun onCompleted(status: AirwallexPaymentStatus) {
              // You can handle different payment statuses and perform UI action respectively here
            }
        }
     )
```
### Retrieve the list of payment methods
```kotlin
     val methods = airwallex.retrieveAvailablePaymentMethods(
        session = session,
        params = RetrieveAvailablePaymentMethodParams.Builder(
           clientSecret = getClientSecretFromSession(session),
           pageNum = 1
        )
            .setActive(true)
            .setTransactionCurrency(session.currency)
            .setCountryCode(session.countryCode)
            .build()
     )
```
### Retrieve the list of saved cards
```kotlin
     val consents = airwallex.retrieveAvailablePaymentConsents(
        RetrieveAvailablePaymentConsentsParams.Builder(
           clientSecret = clientSecret,
           customerId = customerId,
           pageNum = 1
        )
            .setNextTriggeredBy(nextTriggerBy)
            .setStatus(PaymentConsent.PaymentConsentStatus.VERIFIED)
            .build()
     )
```
### Launch payment via Google Pay
Before invoking the payment API, you need to follow the steps to[Set up Google Pay](#set-up-google-pay)
```kotlin
// NOTE: We only support AirwallexPaymentSession (one off session), no recurring session for Google Pay at the moment.
// Also make sure you pass GooglePayOptions to the session. Refer to [Set up Google Pay].
airwallex.startGooglePay(
    session = session,
    listener = object : Airwallex.PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // You can handle different payment statuses and perform UI action respectively here
        }
    }
)
```
### Pay by redirection
```kotlin
airwallex.startRedirectPay(
    session = session,
    paymentType = "alipayhk",
    listener = object : Airwallex.PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
           // You can handle different payment statuses and perform UI action respectively here
        }
    }
)
```

## SDK Example
This sample app demonstrates integrating with the Airwallex Android SDK using its prebuilt UI components to manage the checkout flow, including specifying a shipping address and selecting a Payment Method.

To run the example project, you should follow these steps.

1. Run the following script to clone the repository to your local machine
`git clone git@github.com:airwallex/airwallex-payment-android.git`

2. Open Android Studio and import the project by selecting the `build.gradle` file from the cloned repository

3. Go to [Airwallex Account settings > API keys](https://www.airwallex.com/app/settings/api), then copy `Client ID` and` API key` to [`Settings.kt`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/java/com/airwallex/paymentacceptance/Settings.kt)
```
    private const val BASE_URL = replace_with_base_url
    private const val API_KEY = replace_with_api_key
    private const val CLIENT_ID = replace_with_client_id
```

4. Register app on [WeChat Pay](https://pay.weixin.qq.com/index.php/public/wechatpay), then copy `App ID` to [`Settings.kt`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/java/com/airwallex/paymentacceptance/Settings.kt)
```
    private const val WECHAT_APP_ID = "put your WeChat app id here"
```

5. Run the `sample` project

## Test Card Numbers
https://cardinaldocs.atlassian.net/wiki/spaces/CCen/pages/903577725/EMV+3DS+Test+Cases

## Contributing
We welcome contributions of any kind including new features, bug fixes, and documentation 
