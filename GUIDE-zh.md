# Airwallex Android SDK
本节主要介绍如何集成Airwallex Android SDK。 这个文档假定你是Android开发人员，并且熟悉Android Studio和Gradle。

我们的Demo开源在[Github](https://github.com/airwallex/airwallex-payment-android)，可以帮助你更好地了解如何在你的Android项目中集成Airwallex Android SDK。

开始使用我们的集成指南和示例项目。

## 目录
* [支持版本](#支持版本)
* [开始集成](#开始集成)
    * [准备](#准备)
    * [基本集成](#基本集成)
* [运行Sample](#运行Sample)
* [贡献](#贡献)

## 支持版本
Airwallex Android SDK 支持Android 19及以上版本

## 开始集成
请按照以下步骤开始集成：

### 准备
- 添加依赖
在项目的根目录下，打开`build.gradle`，并添加以下内容：

```groovy
    dependencies {
        implementation 'com.airwallex:airwallex-core:1.0.0'
    }
```

- 配置SDK (可选的)
我们提供了一些参数，可用于调试SDK的，最好在`Application`中调用以下方法

```groovy
    Airwallex.initialize(
        AirwallexConfiguration.Builder()
            .enableLogging(true)            // 在SDK中打开log，需要在发布release版本的时候设置成false
            .setBaseUrl(Settings.baseUrl)   // 你可以修改baseUrl，方便在其他环境测试
            .build()
    )
```

### 基本集成

#### Confirm Payment Intent 完成支付
在confirm `PaymentIntent`之前, 你必须在服务端创建一个`PaymentIntent`对象，并返回到客户端.

> 商户服务端
>1. 首先你需要申请一个令牌来允许你访问所有的API. 通过使用你的Client ID 和 API key (可以在这里找到 [Account settings > API keys](https://www.airwallex.com/app/settings/api)) 来调用Authentication API. 成功之后，会生产一个令牌.
>
>2. 创建 customer(可选的) 允许您保存customer的详细信息, 可以在customer上绑定付款方式，以便在customer在支付时快速检索支持的付款方式。 [`/api/v1/pa/customers/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Customers/_api_v1_pa_customers_create/post)
>
>3. 最终, 你可以通过[`/api/v1/pa/payment_intents/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Payment_Intents/_api_v1_pa_payment_intents_create/post) 来创建一个`PaymentIntent`对象，然后返回到你的客户端

服务端完成以上所有步骤之后，客户端会得到一个`PaymentIntent`对象，然后你就能开始confirm `PaymentIntent`

1. 初始化一个 `Airwallex` 对象, 这个 Airwallex SDK 的入口.

```kotlin
    val airwallex = Airwallex()
```

2. 然后你可以调用 `confirmPaymentIntent` 方法
```kotlin
    airwallex.confirmPaymentIntent(
        params = ConfirmPaymentIntentParams.createWeChatParams(
            paymentIntentId = paymentIntent.id,         // required
            clientSecret = paymentIntent.clientSecret,  // required
            customerId = paymentIntent.customerId       // optional
        ),
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

3. 成功confirm `PaymentIntent`之后
- 微信支付
Airwallex将返回微信支付所需的所有参数。你需要调用 [微信支付](https://pay.weixin.qq.com/wiki/doc/api/wxpay/pay/In-AppPay/chapter6_2.shtml)来完成最终的支付。
可以查看[Sample](https://github.com/airwallex/airwallex-payment-android/tree/master)获得更多信息。
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

- 信用卡支付. 你可以直接提供用户confirm `PaymentIntent` 的结果

#### Retrieve Payment Intent 来确认支付是否成功
confirm完成之后, Airwallex 服务端会通知商户，然后你可以调用`retrievePaymentIntent`方法，并检查结果中的`status`字段来确认支付是否成功
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

## 运行Sample
请按照以下步骤运行我们的`sample`

* **Step 1:** 使用以下命令clone代码到本地
`git clone git@github.com:airwallex/airwallex-payment-android.git`

* **Step 2:** 打开Android Studio并选择`build.gradle`文件来导入项目

* **Step 3:** 打开 [Airwallex Account settings > API keys](https://www.airwallex.com/app/settings/api), 并拷贝 `Client ID` 和` API key` 到 [`strings.xml`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/res/values/strings.xml)

* **Step 4:** 在 [WeChat Pay](https://pay.weixin.qq.com/index.php/public/wechatpay)注册app, 然后拷贝 `App ID` 和 `App Signature` 到 [`strings.xml`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/res/values/strings.xml)

* **Step 5:** 运行`sample`工程

## 贡献
我们欢迎任何形式的贡献，包括新功能，错误修复和文档改进。最简单的方式就是创建pull request - 我们会尽快回复。 如果你发现任何错误或有任何疑问，也可以提交Issues。