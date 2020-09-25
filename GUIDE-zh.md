# Airwallex Android SDK
本节主要介绍如何集成Airwallex Android SDK。 这个文档假定你是Android开发人员，并且熟悉Android Studio和Gradle。

我们的Demo开源在[Github](https://github.com/airwallex/airwallex-payment-android)，可以帮助你更好地了解如何在你的Android项目中集成Airwallex Android SDK。

我们还提供了功能强大的可自定义界面。

<p align="center">
<img src="assets/payment_edit_shipping.jpg" width="20%" alt="PaymentShippingActivity" hspace="10">
<img src="assets/payment_select_payment_method.jpg" width="20%" alt="PaymentMethodsActivity" hspace="10">
<img src="assets/payment_new_card.jpg" width="20%" alt="AddPaymentMethodActivity" hspace="10">
<img src="assets/payment_detail.jpg" width="20%" alt="PaymentCheckoutActivity" hspace="10">
</p>

开始使用我们的集成指南和示例项目。

## 目录
* [支持版本](#支持版本)
* [开始集成](#开始集成)
    * [准备](#准备)
    * [基本集成](#基本集成)
    * [UI集成](#UI集成)
* [运行Sample](#运行Sample)
* [测试卡号](#测试卡号)
* [自定义颜色](#自定义颜色)
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

此外，将以下Maven repository 和 credentials 添加到根目录下的`build.gralde`:
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
    val listener = object : Airwallex.PaymentListener<PaymentIntent> {
        override fun onSuccess(response: PaymentIntent) {
            // Confirm Payment Intent success
        }

        override fun onFailed(exception: AirwallexException) {
            // Confirm Payment Intent failed
        }
    }
    if (paymentMethod.type == PaymentMethodType.WECHAT) {
        val params =  ConfirmPaymentIntentParams.createWeChatParams(
            paymentIntentId = paymentIntent.id,
            clientSecret = requireNotNull(paymentIntent.clientSecret),
            customerId = paymentIntent.customerId
        )
        airwallex.confirmPaymentIntent(this, params, listener)
    } else if (paymentMethod.type == PaymentMethodType.CARD) {
        val params = ConfirmPaymentIntentParams.createCardParams(
            paymentIntentId = paymentIntent.id,
            clientSecret = requireNotNull(paymentIntent.clientSecret),
            paymentMethodReference = PaymentMethodReference(
                requireNotNull(paymentMethod.id),
                requireNotNull(cvc)
            ),
            customerId = paymentIntent.customerId
        )
        airwallex.confirmPaymentIntent(this, params, listener)
    }
```

3. 成功confirm `PaymentIntent`之后
- 微信支付
Airwallex将返回微信支付所需的所有参数。你需要调用 [微信支付](https://pay.weixin.qq.com/wiki/doc/api/wxpay/pay/In-AppPay/chapter6_2.shtml)来完成最终的支付。
可以查看[Sample](https://github.com/airwallex/airwallex-payment-android/tree/master)获得更多信息。
```kotlin
    val weChat = response.weChat
    // `weChat` 包含微信支付所需的所有参数，然后你需要调用[微信支付](https://pay.weixin.qq.com/wiki/doc/api/wxpay/pay/In-AppPay/chapter6_2.shtml).

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

### UI集成
我们提供了一些自定义UI，可以在你的Android App中快速集成支付功能。你可以单独使用某一个界面或某几个界面

- 初始化`AirwallexStarter`
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

- Shipping Info 界面， shipping 字段是可选的。成功保存后，它将返回一个`shipping`对象
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

- Payment Methods 界面, 你需要传入一个`paymentIntent`对象. 这个界面将显示当前用户保存的所有付款方式，你可以选择任何一种进行付款
```kotlin
    private val clientSecretProvider by lazy {
        ExampleClientSecretProvider()
    }
    airwallexStarter.presentSelectPaymentMethodFlow(paymentIntent, clientSecretProvider,
        object : AirwallexStarter.PaymentMethodListener {
            override fun onSuccess(paymentMethod: PaymentMethod, cvc: String?) {
                Log.d(TAG, "Select PaymentMethod success")
            }

            override fun onCancelled() {
                Log.d(TAG, "User cancel select PaymentMethod")
            }
        })
```

- Enter credit card 界面，你需要输入信用卡卡号，过期时间，CVC 来创建 PaymentMethod. 你需要传入一个`paymentIntent`对象。
```kotlin
    private val clientSecretProvider by lazy {
        ExampleClientSecretProvider()
    }
    airwallexStarter.presentAddPaymentMethodFlow(paymentIntent, clientSecretProvider,
        object : AirwallexStarter.PaymentMethodListener {
            override fun onSuccess(paymentMethod: PaymentMethod, cvc: String?) {
                Log.d(TAG, "Create PaymentMethod success")
            }

            override fun onCancelled() {
                Log.d(TAG, "User cancel create PaymentMethod")
            }
        })
```

- Payment Detail 界面，你需要传入一个`paymentIntent`对象和一个`paymentMethod`对象。界面会显示当前付款金额已经支付方式等数据，支付完成之后，将通过回调方法返回`PaymentIntent`或`AirwallexError`
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

- 使用整个 Payment Flow, 需要传入一个`paymentIntent`对象. 你可以通过调用此方法来完成整个付款过程，支付完成之后，将通过回调方法返回`PaymentIntent`或`AirwallexError`
```kotlin
    private val clientSecretProvider by lazy {
        ExampleClientSecretProvider()
    }
    airwallexStarter.presentPaymentFlow(paymentIntent, clientSecretProvider,
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

## 运行Sample
请按照以下步骤运行我们的`sample`

* **Step 1:** 使用以下命令clone代码到本地
`git clone git@github.com:airwallex/airwallex-payment-android.git`

* **Step 2:** 打开Android Studio并选择`build.gradle`文件来导入项目

* **Step 3:** 打开 [Airwallex Account settings > API keys](https://www.airwallex.com/app/settings/api), 并拷贝 `Client ID` 和` API key` 到 [`Settings.kt`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/java/com/airwallex/paymentacceptance/Settings.kt)
```
    private const val AUTH_URL = "put your auth url here"
    private const val BASE_URL = "put your base url here"
    private const val API_KEY = "put your api key here"
    private const val CLIENT_ID = "put your client id here"
```

* **Step 4:** 在 [WeChat Pay](https://pay.weixin.qq.com/index.php/public/wechatpay)注册app, 然后拷贝 `App ID` 和 `App Signature` 到 [`Settings.kt`](https://github.com/airwallex/airwallex-payment-android/blob/master/sample/src/main/java/com/airwallex/paymentacceptance/Settings.kt)
```
    private const val WECHAT_APP_ID = "put your WeChat app id here"
    private const val WECHAT_APP_SIGNATURE = "put your WeChat app signature here"
```

* **Step 5:** 运行`sample`工程

## 测试卡号
- 4242 4242 4242 4242
- 2223 0000 1018 1375 Expire: 12/2022 CVC: 650 (3DS 1.0)
- 4012 0003 0000 1003 Month: 12 (3DS 1.0)
- 4012 0003 0000 1003 (3DS 2.0)

## 自定义颜色
您可以在应用程序中覆盖这些颜色值, 用来适配您的应用风格
```
    <color name="airwallex_color_accent">#131415</color>
    <color name="airwallex_color_primary">#E66060</color>
    <color name="airwallex_color_primary_dark">#F52626</color>
```

## 贡献
我们欢迎任何形式的贡献，包括新功能，错误修复和文档改进。最简单的方式就是创建pull request - 我们会尽快回复。 如果你发现任何错误或有任何疑问，也可以提交Issues。