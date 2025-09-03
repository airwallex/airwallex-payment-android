# 开发指南

Airwallex Android SDK 是一款灵活的工具，能够帮助你在 Android 应用中集成多种支付方式。它同时包含预构建的 UI，你可以灵活选择只用部分组件或全流程。

本指南将帮助你完成 Airwallex Android SDK 的集成。我们假设你是 Android 开发者，并熟悉 Android Studio 和 Gradle。

要通过 Airwallex Android SDK 实现在线支付，请先完成准备工作，并根据实际需求选择合适的集成方式。

## 目录

- [概述](#概述)
  - [Airwallex API](#airwallex-api)
  - [Airwallex 原生 UI](#airwallex-原生-ui)
- [集成准备](#集成准备)
- [原生 UI 集成](#原生-ui-集成)
  - [环境要求](#环境要求)
  - [安装 SDK](#安装-sdk)
  - [SDK 配置](#sdk-配置)
  - [支付流程](#支付流程)
  - [Google Pay 集成](#google-pay-集成)
  - [自定义主题](#自定义主题)
- [低层 API 集成](#低层-api-集成)
  - [步骤 1：SDK 安装](#步骤-1sdk-安装)
  - [步骤 2：配置及准备](#步骤-2配置及准备)
  - [步骤 3：创建 AirwallexSession 和 Airwallex 对象](#步骤-3创建-airwallexsession-和-airwallex-对象)
  - [用卡和账单详情确认支付](#用卡和账单详情确认支付)
  - [用 Consent ID 确认支付](#用-consent-id-确认支付)
  - [用 PaymentConsent 确认支付](#用-paymentconsent-确认支付)
  - [获取支付方式列表](#获取支付方式列表)
  - [获取已保存卡列表](#获取已保存卡列表)
  - [通过 Google Pay 发起支付](#通过-google-pay-发起支付)
  - [重定向支付](#重定向如支付宝hk)
- [SDK 示例](#sdk-示例)
- [测试卡号](#测试卡号)
- [贡献](#贡献)

---

## 概述

### Airwallex API

Airwallex Android SDK 是一个灵活工具，可集成多种支付方式到你的 Android 应用。

> 兼容 Android API 21 及以上，SDK 文件约 3188.04KB

支持的支付方式：
- 银行卡：Visa、Mastercard（如无需原生 UI 仅用 API，网站需 PCI-DSS 合规）
- 钱包类：支付宝、支付宝HK、DANA、GCash、Kakao Pay、Touch 'n Go、微信支付

### Airwallex 原生 UI

Airwallex 原生 UI 是预构建 UI，支持自定义颜色，适配你的 App 主题。你可以只用部分组件，也可集成完整流程。

|序号|UI组件|说明|
|---|---|---|
|1|[`编辑收货信息页面`](#edit-shipping-info)<br/>详细收货表单，填写后返回收货信息对象|<p align="center"><img src="https://github.com/user-attachments/assets/4c3c2344-3dec-4379-980e-28ee948c3f28" width="90%" alt="PaymentShippingActivity" hspace="10"></p>|
|2|[`选择支付方式页面`](#selecting-payment-method-page)</br>展示所有可用支付方式，用户可选择支付|<p align="center"><img src="https://github.com/user-attachments/assets/71f526f5-ce3e-4615-a0ec-a75689bd9eb5" width="90%" alt="PaymentMethodsActivity" hspace="10"></p>|
|3|[`输入卡信息模块`](#input-card-information-module)</br>卡号、有效期、CVV|<p align="center"><img src="https://github.com/user-attachments/assets/010be7e0-2395-4d10-bb18-fca7df9712d6" width="90%" alt="AddPaymentMethodActivity" hspace="10"></p>|
|4|[`确认支付页面`](#confirm-payment-intent-page)<br/>传入 PaymentIntent 和 PaymentMethod，展示金额并执行支付操作|<p align="center"><img src="assets/payment_detail.jpg" width="90%" alt="PaymentCheckoutActivity" hspace="10"></p>|

---

## 集成准备

我们提供两种集成方式：  
1. 可直接调起预构建的用户界面  
2. 使用低层 API，自定义 UI

---

## 原生 UI 集成

### 环境要求

- Android API 21 及以上

### 安装 SDK

SDK 已发布至 [Maven Central](https://repo1.maven.org/maven2/io/github/airwallex/)

在 app 级 `build.gradle` 添加依赖：

```groovy
dependencies {
    // 必须
    implementation 'io.github.airwallex:payment:6.2.4'
    // 按需添加支付方式
    implementation 'io.github.airwallex:payment-card:6.2.4'
    implementation 'io.github.airwallex:payment-redirect:6.2.4'
    implementation 'io.github.airwallex:payment-wechat:6.2.4'
    implementation 'io.github.airwallex:payment-googlepay:6.2.4'
}
```

### SDK 配置

在 Application 类初始化 SDK：

```kotlin
AirwallexStarter.initialize(
    application,
    AirwallexConfiguration.Builder()
        .enableLogging(true)        // 生产环境请设为 false
        .saveLogToLocal(false)      // 如需自定义日志存储请设为 false
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

### 支付流程

#### 1. 创建 PaymentIntent（服务端）

1. 获取 access token：用 Client ID 和 API key 调用认证 API（见 [API keys 设置](https://www.airwallex.com/app/settings/api)）
2. 创建客户（可选）：用 [`/api/v1/pa/customers/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Customers/_api_v1_pa_customers_create/post)
3. 创建 PaymentIntent：用 [`/api/v1/pa/payment_intents/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Payment_Intents/_api_v1_pa_payment_intents_create/post) 并获得 `client_secret`

#### 2. 创建 Airwallex Session

根据业务场景创建 session 对象：

##### 标准支付 Session

```kotlin
val paymentSession = AirwallexPaymentSession.Builder(
    paymentIntent = paymentIntent,
    countryCode = countryCode,
    googlePayOptions = googlePayOptions // 可选
)
    .setRequireBillingInformation(true)
    .setRequireEmail(requireEmail)
    .setReturnUrl(returnUrl)
    .setAutoCapture(autoCapture)
    .setHidePaymentConsents(false)
    .setPaymentMethods(listOf()) // 空列表表示所有可用方式
    .build()
```

##### 循环支付 Session

```kotlin
val recurringSession = AirwallexRecurringSession.Builder(
    customerId = customerId,
    clientSecret = clientSecret,
    currency = currency,
    amount = amount,
    nextTriggerBy = nextTriggerBy,
    countryCode = countryCode
)
    .setRequireEmail(requireEmail)
    .setShipping(shipping)
    .setRequireCvc(requireCVC)
    .setMerchantTriggerReason(merchantTriggerReason)
    .setReturnUrl(returnUrl)
    .setPaymentMethods(listOf())
    .build()
```

##### 循环支付（带 Intent）Session

```kotlin
val recurringWithIntentSession = AirwallexRecurringWithIntentSession.Builder(
    paymentIntent = paymentIntent,
    customerId = customerId,
    nextTriggerBy = nextTriggerBy,
    countryCode = countryCode
)
    .setRequireEmail(requireEmail)
    .setRequireCvc(requireCVC)
    .setMerchantTriggerReason(merchantTriggerReason)
    .setReturnUrl(returnUrl)
    .setAutoCapture(autoCapture)
    .setPaymentMethods(listOf())
    .build()
```

#### 3. 展示支付 UI

##### 完整支付流程

```kotlin
AirwallexStarter.presentEntirePaymentFlow(
    activity = activity,
    session = session,
    paymentResultListener = object : Airwallex.PaymentResultListener { 
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // 处理支付结果
        }
    }
)
```

##### 仅卡支付流程

```kotlin
AirwallexStarter.presentCardPaymentFlow(
    activity = activity,
    session = session,
    paymentResultListener = object : Airwallex.PaymentResultListener { 
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // 处理支付结果
        }
    }
)
```

##### 卡支付弹窗

```kotlin
val dialog = AirwallexAddPaymentDialog(
    activity = activity,
    session = session,
    paymentResultListener = object : Airwallex.PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // 处理支付结果
        }
    }
)
dialog.show()
```

#### 4. 收货信息

允许用户填写收货信息：

```kotlin
AirwallexStarter.presentShippingFlow(
    activity = activity,
    shipping = shipping, // 可选
    shippingResultListener = object : Airwallex.ShippingResultListener {
        override fun onCompleted(status: AirwallexShippingStatus) {
            // 处理结果
        }
    }
)
```

#### 5. 校验支付状态

支付完成后校验状态：

```kotlin
airwallex.retrievePaymentIntent(
    params = RetrievePaymentIntentParams(
        paymentIntentId = paymentIntentId,
        clientSecret = clientSecret
    ),
    listener = object : Airwallex.PaymentListener<PaymentIntent> {
        override fun onSuccess(response: PaymentIntent) {
            // 成功回调
        }
        override fun onFailed(exception: AirwallexException) {
            Log.e(TAG, "获取 PaymentIntent 失败", exception)
        }
    }
)
```

### Google Pay 集成

#### 配置步骤

1. 确认你的 Airwallex 账号已开通 Google Pay
2. 安装 SDK 时按 [安装 SDK](#安装-sdk) 添加 Google Pay 模块

#### 自定义

可通过 `GooglePayOptions` 配置：

```kotlin
val googlePayOptions = GooglePayOptions(
    allowedCardAuthMethods = listOf("CRYPTOGRAM_3DS"),
    billingAddressParameters = BillingAddressParameters(BillingAddressParameters.Format.FULL),
    shippingAddressParameters = ShippingAddressParameters(listOf("AU", "CN"), true)
)
```

#### 支持卡类型

Google Pay 支持如下卡类型：
- AMEX
- DISCOVER
- JCB
- MASTERCARD
- VISA
- MAESTRO（仅当 `countryCode` 为 `BR`）

### 自定义主题

可覆盖如下颜色值，适配你的应用风格：
```
<color name="airwallex_tint_color">@color/airwallex_color_red</color>
```
详见 [Android 主题自定义](https://developer.android.com/guide/topics/ui/look-and-feel/themes#CustomizeTheme)

---

## 低层 API 集成

你可以基于低层 API 完全自定义 UI。

### 步骤 1：SDK 安装

SDK 支持 Android API 21 及以上。

在 app 级 `build.gradle` 添加依赖：

```groovy
dependencies {
    // 必须
    implementation 'io.github.airwallex:payment-components-core:6.2.4'
    // 按需添加支付方式
    implementation 'io.github.airwallex:payment-card:6.2.4'
    implementation 'io.github.airwallex:payment-googlepay:6.2.4'
    implementation 'io.github.airwallex:payment-redirect:6.2.4'
}
```

### 步骤 2：配置及准备

初始化 SDK：

```kotlin
Airwallex.initialize(
    this,
    AirwallexConfiguration.Builder()
        .enableLogging(true) // 生产环境建议为 false
        .saveLogToLocal(false)
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

服务端需提前创建 PaymentIntent，详见 [支付流程](#支付流程)。

### 步骤 3：创建 AirwallexSession 和 Airwallex 对象

创建 AirwallexSession 参考上文，创建 Airwallex 对象如下：

```kotlin
val airwallex = Airwallex(activity)
```

#### 用卡和账单详情确认支付

```kotlin
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
    saveCard = false,
    listener = object : Airwallex.PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // 处理不同支付状态
        }
    }
)
```

#### 用 Consent ID 确认支付

```kotlin
airwallex.confirmPaymentIntent(
    session = session,
    paymentConsentId = "cst_xxxxxxxxxx",
    listener = object : Airwallex.PaymentResultListener { 
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // 处理不同支付状态
        }
    }
)
```

#### 用 PaymentConsent 确认支付

```kotlin
airwallex.confirmPaymentIntent(
    session = session as AirwallexPaymentSession,
    paymentConsent = paymentConsent,
    listener = object : Airwallex.PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // 处理不同支付状态
        }
    }
)
```

#### 获取支付方式列表

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

#### 获取已保存卡列表

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

#### 通过 Google Pay 发起支付

请先完成 [Google Pay 配置](#google-pay-集成)

```kotlin
airwallex.startGooglePay(
    session = session,
    listener = object : Airwallex.PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // 处理不同支付状态
        }
    }
)
```

#### 重定向（如支付宝HK）

```kotlin
airwallex.startRedirectPay(
    session = session,
    paymentType = "alipayhk",
    listener = object : Airwallex.PaymentResultListener {
        override fun onCompleted(status: AirwallexPaymentStatus) {
            // 处理不同支付状态
        }
    }
)
```

---

## SDK 示例

本示例应用演示使用 Airwallex Android SDK 内置 UI 组件管理结账流程，包括填写收货地址和选择支付方式。

运行步骤：

1. 克隆项目到本地：
   ```
   git clone git@github.com:airwallex/airwallex-payment-android.git
   ```
2. 打开 Android Studio，选择项目根目录下的 `build.gradle` 导入项目
3. 前往 [Airwallex Account settings > API keys](https://www.airwallex.com/app/settings/api)，将 Client ID 和 API key 填入 [`Settings.kt`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/java/com/airwallex/sample/Settings.kt)：
   ```kotlin
   private const val BASE_URL = replace_with_base_url
   private const val API_KEY = replace_with_api_key
   private const val CLIENT_ID = replace_with_client_id
   ```
4. 微信支付需注册 App 并将 App ID 填入 [`Settings.kt`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/java/com/airwallex/sample/Settings.kt)：
   ```kotlin
   private const val WECHAT_APP_ID = "put your WeChat app id here"
   ```
5. 运行 `sample` 工程

---

## 测试卡号

详见：
https://cardinaldocs.atlassian.net/wiki/spaces/CCen/pages/903577725/EMV+3DS+Test+Cases

---

## 贡献

欢迎任何形式的贡献，包括新功能、bug修复和文档改进。最简单的方式是创建 pull request，我们会尽快回复。如果你发现任何错误或有建议，欢迎提交 issue。

---

如需 Markdown 源文件，可直接复制上方内容到 GUIDE-zh.md。
