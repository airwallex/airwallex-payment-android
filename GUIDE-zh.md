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
  - [自定义外观](#自定义外观)
  - [支付流程](#支付流程)
  - [Google Pay 集成](#google-pay-集成)
- [嵌入式元素集成](#嵌入式元素集成)
  - [概述](#嵌入式概述)
  - [环境要求](#嵌入式环境要求)
  - [安装 SDK](#嵌入式安装-sdk)
  - [创建 PaymentElement](#创建-paymentelement)
  - [配置选项](#配置选项)
  - [Kotlin 示例](#kotlin-示例)
  - [Java 示例](#java-示例)
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
    implementation 'io.github.airwallex:payment:6.4.2'
    // 按需添加支付方式
    implementation 'io.github.airwallex:payment-card:6.4.2'
    implementation 'io.github.airwallex:payment-redirect:6.4.2'
    implementation 'io.github.airwallex:payment-wechat:6.4.2'
    implementation 'io.github.airwallex:payment-googlepay:6.4.2'
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

## 自定义外观

你可以自定义 Airwallex SDK UI 的外观，包括主题色和深色模式偏好设置。这适用于原生 UI 集成和嵌入式元素集成。

### 主题色和深色模式

使用 `PaymentAppearance` 配置支付 UI 外观：

**Kotlin:**
```kotlin
import com.airwallex.android.core.AirwallexConfiguration
import com.airwallex.android.core.PaymentAppearance

AirwallexStarter.initialize(
    application,
    AirwallexConfiguration.Builder()
        .enableLogging(true)
        .setEnvironment(environment)
        .setSupportComponentProviders(
            listOf(
                CardComponent.PROVIDER,
                RedirectComponent.PROVIDER,
                GooglePayComponent.PROVIDER
            )
        )
        .setPaymentAppearance(
            PaymentAppearance(
                themeColor = 0xFF612FFF.toInt(),  // 自定义主题色（ARGB 格式）
                isDarkTheme = true                 // 强制深色模式（true）、浅色模式（false）或跟随系统（null）
            )
        )
        .build()
)
```

**Java:**
```java
import com.airwallex.android.core.AirwallexConfiguration;
import com.airwallex.android.core.PaymentAppearance;

AirwallexStarter.initialize(
    application,
    new AirwallexConfiguration.Builder()
        .enableLogging(true)
        .setEnvironment(environment)
        .setSupportComponentProviders(
            Arrays.asList(
                CardComponent.PROVIDER,
                RedirectComponent.PROVIDER,
                GooglePayComponent.PROVIDER
            )
        )
        .setPaymentAppearance(
            new PaymentAppearance(
                0xFF612FFF,  // 自定义主题色（ARGB 格式）
                true         // 强制深色模式（true）、浅色模式（false）或跟随系统（null）
            )
        )
        .build()
);
```

**PaymentAppearance 选项：**
- `themeColor`：自定义主题色，ARGB 格式（如 `0xFF612FFF`）。如为 null，则使用默认 Airwallex 主题色。
- `isDarkTheme`：
  - `true` - 强制深色模式
  - `false` - 强制浅色模式
  - `null` - 跟随系统深色模式设置（默认）

### 传统主题覆盖

你也可以使用 Android 主题系统覆盖默认主题色：

```xml
<color name="airwallex_tint_color">@color/your_custom_color</color>
```

注意：推荐使用 `PaymentAppearance` 方式，因为它提供更多控制并且在所有 SDK UI 组件中保持一致。

### 支付流程

Airwallex Android SDK 支持两种支付流程：

1. **标准流程**：在展示支付 UI 前，先在服务端创建 PaymentIntent。这是传统方式，适用于支付金额和详情已确定的场景。

2. **Express Checkout（快速结账）**：提供 `PaymentIntentProvider` 或 `PaymentIntentSource`，在收集完用户支付详情后按需创建 PaymentIntent。适用于以下场景：
   - 希望减少前置服务端调用
   - 希望降低 PaymentIntent 过期风险，仅在用户主动进行支付时才创建
   - 希望避免为早期放弃结账流程的用户创建不必要的 PaymentIntent
   - 需要在支付时实时验证库存或商品可用性

两种流程都完全支持，你可以选择最适合你业务场景的方式。

#### 1. 创建 PaymentIntent（服务端）

对于**标准流程**，需要在展示支付 UI 前在服务端创建 PaymentIntent。

对于 **Express Checkout**，你将在 `PaymentIntentProvider` 或 `PaymentIntentSource` 实现中，当 SDK 请求时创建 PaymentIntent。

1. 获取 access token：用 Client ID 和 API key 调用认证 API（见 [API keys 设置](https://www.airwallex.com/app/settings/api)）
2. 创建客户（可选）：用 [`/api/v1/pa/customers/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Customers/_api_v1_pa_customers_create/post)
3. 创建 PaymentIntent：用 [`/api/v1/pa/payment_intents/create`](https://www.airwallex.com/docs/api#/Payment_Acceptance/Payment_Intents/_api_v1_pa_payment_intents_create/post) 并获得 `client_secret`

#### 2. 创建 Airwallex Session

根据业务场景创建 session 对象。你可以选择两种方式：
- **标准流程**：预先在服务端创建 PaymentIntent，然后传给 SDK
- **Express Checkout**：提供 PaymentIntentProvider 在收集完支付详情后按需创建 PaymentIntent

##### 标准支付 Session

**标准流程**（预先创建 PaymentIntent）：
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

**Express Checkout 流程**（按需创建 PaymentIntent）：
```kotlin
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.PaymentIntentProvider
import com.airwallex.android.core.PaymentIntentSource


// 方式 1：使用 PaymentIntentProvider（基于回调，兼容 Java）
class MyPaymentIntentProvider : PaymentIntentProvider {
    override val currency: String = "USD"
    override val amount: BigDecimal = 100.toBigDecimal()

    override fun provide(callback: PaymentIntentProvider.PaymentIntentCallback) {
        // 在需要时调用 API 创建 PaymentIntent
        myApiService.createPaymentIntent { result ->
            when (result) {
                is Success -> callback.onSuccess(result.paymentIntent)
                is Error -> callback.onError(result.exception)
            }
        }
    }
}

val provider = MyPaymentIntentProvider()
val session = AirwallexPaymentSession.Builder(
    paymentIntentProvider = provider,
    countryCode = countryCode,
    customerId = customerId, // 可选
    googlePayOptions = googlePayOptions // 可选
)
    .setRequireBillingInformation(true)
    .setRequireEmail(requireEmail)
    .setReturnUrl(returnUrl)
    .setAutoCapture(autoCapture)
    .setHidePaymentConsents(false)
    .setPaymentMethods(listOf())
    .build()


// 方式 2：使用 PaymentIntentSource（基于 suspend，推荐 Kotlin 使用）
class MyPaymentIntentSource : PaymentIntentSource {
    override val currency: String = "USD"
    override val amount: BigDecimal = 100.toBigDecimal()

    override suspend fun getPaymentIntent(): PaymentIntent {
        // 使用 suspend 函数调用 API
        return myApiService.createPaymentIntent()
    }
}

val source = MyPaymentIntentSource()
val session = AirwallexPaymentSession.Builder(
    paymentIntentSource = source,
    countryCode = countryCode,
    customerId = customerId, // 可选
    googlePayOptions = googlePayOptions // 可选
)
    .setRequireBillingInformation(true)
    .setRequireEmail(requireEmail)
    .setReturnUrl(returnUrl)
    .setAutoCapture(autoCapture)
    .setHidePaymentConsents(false)
    .setPaymentMethods(listOf())
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

**标准流程**（预先创建 PaymentIntent）：
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

**Express Checkout 流程**（按需创建 PaymentIntent）：
```kotlin
import com.airwallex.android.core.AirwallexRecurringWithIntentSession
import com.airwallex.android.core.PaymentIntentProvider
import com.airwallex.android.core.PaymentIntentSource


// 方式 1：使用 PaymentIntentProvider
val provider = MyPaymentIntentProvider() // 同上文的实现
val session = AirwallexRecurringWithIntentSession.Builder(
    paymentIntentProvider = provider,
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


// 方式 2：使用 PaymentIntentSource（推荐 Kotlin 使用）
val source = MyPaymentIntentSource() // 同上文的实现
val session = AirwallexRecurringWithIntentSession.Builder(
    paymentIntentSource = source,
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

## 嵌入式元素集成

Airwallex SDK 提供 `PaymentElement` - 一个灵活的组件，允许你将支付 UI 直接嵌入到自己的 activity 或 view 中。这让你可以完全控制宿主 UI，同时利用 Airwallex 预构建的支付组件。

### <a name="嵌入式概述"></a>概述

与原生 UI 集成中 SDK 启动自己的 activity（`PaymentMethodsActivity`、`AddPaymentActivity`）不同，嵌入式元素集成让你可以：
- 在自己的 activity/view 中嵌入支付 UI
- 控制周围的 UI 和布局
- 自定义容器样式
- 无缝集成到你的应用导航流程中

两种集成方式都支持通过 `PaymentAppearance` 进行相同的自定义选项（主题色和深色模式）。

### <a name="嵌入式环境要求"></a>环境要求

- Android API 21 及以上
- 支持 Kotlin 协程（用于 suspend 函数）

### <a name="嵌入式安装-sdk"></a>安装 SDK

添加与原生 UI 集成相同的依赖：

```groovy
dependencies {
    // 核心模块（必需）
    implementation 'io.github.airwallex:payment:6.4.2'

    // 添加你想要支持的支付方式
    implementation 'io.github.airwallex:payment-card:6.4.2'
    implementation 'io.github.airwallex:payment-redirect:6.4.2'
    implementation 'io.github.airwallex:payment-wechat:6.4.2'
    implementation 'io.github.airwallex:payment-googlepay:6.4.2'
}
```

在 Application 类中配置 SDK（与原生 UI 集成相同 - 参见 [SDK 配置](#sdk-配置)）。

### <a name="创建-paymentelement"></a>创建 PaymentElement

`PaymentElement.create()` 是一个 suspend 函数，用于初始化并获取支付 UI 所需的数据。你可以使用 `PaymentFlowListener` 接口或 lambda 回调。

两种方式都返回 `Result<PaymentElement>`，包含：
- `Success` - PaymentElement 实例
- `Failure` - 错误信息

### <a name="配置选项"></a>配置选项

使用 `PaymentElementConfiguration` 配置支付 UI：

#### 1. 仅卡支付（`PaymentElementConfiguration.Card`）

仅显示卡输入和已保存的卡：

```kotlin
import com.airwallex.android.core.AirwallexSupportedCard

// 使用默认配置（所有支持的卡：Visa、Amex、Mastercard、Discover、JCB、Diners Club、UnionPay）
val configuration = PaymentElementConfiguration.Card()

// 或自定义支持的卡品牌
val customConfiguration = PaymentElementConfiguration.Card(
    supportedCardBrands = listOf(
        AirwallexSupportedCard.VISA,
        AirwallexSupportedCard.MASTERCARD
    )
)
```

**注意：** 默认情况下，`supportedCardBrands` 包含 `AirwallexSupportedCard` 中的所有卡（Visa、Amex、Mastercard、Discover、JCB、Diners Club、UnionPay）。你可以自定义此列表以限制接受哪些卡品牌。

#### 2. 支付单（`PaymentElementConfiguration.PaymentSheet`）

以标签页或手风琴布局显示多种支付方式：

```kotlin
val configuration = PaymentElementConfiguration.PaymentSheet(
    layout = PaymentMethodsLayoutType.TAB,           // TAB 或 ACCORDION
    showsGooglePayAsPrimaryButton = true             // true: 将 Google Pay 显示为主按钮，false: 在列表中显示
)
```

**布局选项：**
- `PaymentMethodsLayoutType.TAB` - 支付方式的标签页布局
- `PaymentMethodsLayoutType.ACCORDION` - 支付方式的手风琴布局

**Google Pay 显示：**
- `showsGooglePayAsPrimaryButton = true` - Google Pay 显示为其他支付方式上方的突出按钮
- `showsGooglePayAsPrimaryButton = false` - Google Pay 与其他支付方式一起显示在列表中

### <a name="kotlin-示例"></a>Kotlin 示例

这是在你自己的 activity 中嵌入支付元素的完整示例：

```kotlin
import android.os.Bundle
import android.view.View
import androidx.activity.ComponentActivity
import androidx.lifecycle.lifecycleScope
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentSession
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.PaymentMethodsLayoutType
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.view.composables.PaymentElement
import com.airwallex.android.view.composables.PaymentElementConfiguration
import com.yourapp.databinding.ActivityCheckoutBinding
import kotlinx.coroutines.launch

class CheckoutActivity : ComponentActivity() {

    private lateinit var binding: ActivityCheckoutBinding
    private val airwallex: Airwallex by lazy { Airwallex(this) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckoutBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupPaymentElement()
    }

    private fun setupPaymentElement() {
        // 显示加载指示器
        binding.progressBar.visibility = View.VISIBLE
        binding.composeView.visibility = View.GONE

        lifecycleScope.launch {
            // 创建 session（参见"创建 Airwallex Session"部分）
            val paymentIntent = PaymentIntent(
                id = "your_payment_intent_id",
                clientSecret = "your_client_secret",
                amount = 100.toBigDecimal(),
                currency = "USD"
            )

            val session = AirwallexPaymentSession.Builder(
                paymentIntent = paymentIntent,
                countryCode = "US"
            ).build()

            // 配置支付元素
            val configuration = PaymentElementConfiguration.PaymentSheet(
                layout = PaymentMethodsLayoutType.TAB,
                showsGooglePayAsPrimaryButton = true
            )

            // 创建 PaymentElement
            val result = PaymentElement.create(
                session = session,
                airwallex = airwallex,
                configuration = configuration,
                onLoadingStateChanged = { isLoading ->
                    // 可选：处理支付过程中的加载状态变化
                    binding.progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
                },
                onPaymentResult = { status ->
                    // 处理支付结果
                    when (status) {
                        is AirwallexPaymentStatus.Success -> {
                            // 支付成功
                            showSuccess(status.paymentIntentId)
                        }
                        is AirwallexPaymentStatus.Failure -> {
                            // 支付失败
                            showError(status.exception.message)
                        }
                        is AirwallexPaymentStatus.Cancel -> {
                            // 用户取消支付
                            showCancelled()
                        }
                        is AirwallexPaymentStatus.InProgress -> {
                            // 支付进行中（例如等待 3DS）
                            // 加载由 onLoadingStateChanged 处理
                        }
                    }
                },
                onError = { throwable ->
                    // 可选：处理元素初始化或支付过程中的错误
                    // 如果不提供，SDK 将显示默认错误对话框
                    showError(throwable.message)
                }
            )

            // 处理创建结果
            result.onSuccess { paymentElement ->
                // 隐藏加载，显示支付元素
                binding.progressBar.visibility = View.GONE
                binding.composeView.visibility = View.VISIBLE

                // 渲染支付 UI
                binding.composeView.setContent {
                    paymentElement.Content()
                }
            }.onFailure { throwable ->
                // 初始化支付元素失败
                binding.progressBar.visibility = View.GONE
                showError(throwable.message)
            }
        }
    }

    private fun showSuccess(paymentIntentId: String?) {
        // 显示成功 UI
    }

    private fun showError(message: String?) {
        // 显示错误 UI
    }

    private fun showCancelled() {
        // 处理取消
    }
}
```

**布局 XML (activity_checkout.xml)：**

```xml
<?xml version="1.0" encoding="utf-8"?>
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical"
    android:padding="16dp">

    <!-- 你的自定义 UI 元素 -->
    <TextView
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:text="支付方式"
        android:textSize="20sp"
        android:textStyle="bold"
        android:layout_marginBottom="16dp" />

    <!-- 加载指示器 -->
    <ProgressBar
        android:id="@+id/progressBar"
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="center"
        android:visibility="gone" />

    <!-- PaymentElement 的 ComposeView -->
    <androidx.compose.ui.platform.ComposeView
        android:id="@+id/composeView"
        android:layout_width="match_parent"
        android:layout_height="wrap_content" />

</LinearLayout>
```

或者你可以查看我们示例应用中的 `EmbeddedElementActivity`。

### <a name="java-示例"></a>Java 示例

以下是使用基于监听器的 API 的等效 Java 示例：

```java
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import androidx.activity.ComponentActivity;
import com.airwallex.android.core.Airwallex;
import com.airwallex.android.core.AirwallexPaymentSession;
import com.airwallex.android.core.AirwallexPaymentStatus;
import com.airwallex.android.core.PaymentMethodsLayoutType;
import com.airwallex.android.core.model.PaymentIntent;
import com.airwallex.android.view.PaymentFlowListener;
import com.airwallex.android.view.composables.PaymentElement;
import com.airwallex.android.view.composables.PaymentElementConfiguration;
import com.yourapp.databinding.ActivityCheckoutBinding;
import java.math.BigDecimal;
import kotlin.coroutines.Continuation;
import kotlin.coroutines.CoroutineContext;
import kotlin.coroutines.EmptyCoroutineContext;
import kotlinx.coroutines.BuildersKt;
import kotlinx.coroutines.CoroutineScope;
import org.jetbrains.annotations.NotNull;

public class CheckoutActivity extends ComponentActivity {

    private ActivityCheckoutBinding binding;
    private Airwallex airwallex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCheckoutBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        airwallex = new Airwallex(this);
        setupPaymentElement();
    }

    private void setupPaymentElement() {
        // 显示加载指示器
        binding.progressBar.setVisibility(View.VISIBLE);
        binding.composeView.setVisibility(View.GONE);

        // 创建 session
        PaymentIntent paymentIntent = new PaymentIntent(
            "your_payment_intent_id",
            "your_client_secret",
            new BigDecimal("100"),
            "USD"
        );

        AirwallexPaymentSession session = new AirwallexPaymentSession.Builder(
            paymentIntent,
            "US"
        ).build();

        // 配置支付元素
        PaymentElementConfiguration configuration = new PaymentElementConfiguration.PaymentSheet(
            PaymentMethodsLayoutType.TAB,
            true  // showsGooglePayAsPrimaryButton
        );

        // 创建支付流程监听器
        PaymentFlowListener listener = new PaymentFlowListener() {
            @Override
            public void onLoadingStateChanged(boolean isLoading, @NotNull Context context) {
                // 可选：处理支付过程中的加载状态变化
                binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            }

            @Override
            public void onPaymentResult(@NotNull AirwallexPaymentStatus status) {
                // 处理支付结果
                if (status instanceof AirwallexPaymentStatus.Success) {
                    AirwallexPaymentStatus.Success success = (AirwallexPaymentStatus.Success) status;
                    showSuccess(success.getPaymentIntentId());
                } else if (status instanceof AirwallexPaymentStatus.Failure) {
                    AirwallexPaymentStatus.Failure failure = (AirwallexPaymentStatus.Failure) status;
                    showError(failure.getException().getMessage());
                } else if (status instanceof AirwallexPaymentStatus.Cancel) {
                    showCancelled();
                } else if (status instanceof AirwallexPaymentStatus.InProgress) {
                    // 支付进行中（加载由 onLoadingStateChanged 处理）
                }
            }

            @Override
            public void onError(@NotNull Throwable exception, @NotNull Context context) {
                // 可选：处理错误
                // 如果不提供，SDK 将显示默认错误对话框
                showError(exception.getMessage());
            }
        };

        // 使用协程创建 PaymentElement
        CoroutineScope scope = androidx.lifecycle.LifecycleKt.getLifecycleScope(this);
        BuildersKt.launch(scope, EmptyCoroutineContext.INSTANCE, null, (coroutineScope, continuation) -> {
            // 调用 create suspend 函数
            Result<PaymentElement> result = PaymentElement.create(
                session,
                airwallex,
                configuration,
                listener,
                continuation
            );

            // 处理结果
            if (result.isSuccess()) {
                PaymentElement paymentElement = result.getResultOrNull();
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    binding.composeView.setVisibility(View.VISIBLE);

                    // 渲染支付 UI
                    binding.composeView.setContent(() -> {
                        paymentElement.Content();
                        return null;
                    });
                });
            } else {
                Throwable error = result.exceptionOrNull();
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    showError(error != null ? error.getMessage() : "未知错误");
                });
            }

            return null;
        });
    }

    private void showSuccess(String paymentIntentId) {
        // 显示成功 UI
    }

    private void showError(String message) {
        // 显示错误 UI
    }

    private void showCancelled() {
        // 处理取消
    }
}
```

**与原生 UI 集成的主要区别：**

| 功能 | 原生 UI 集成 | 嵌入式元素集成 |
|------|------------|---------------|
| 入口点 | `AirwallexStarter.presentPaymentFlow()` | `PaymentElement.create()` |
| Activity 所有权 | SDK 拥有 activity | 你拥有 activity |
| UI 容器 | SDK 的 activity | 你的 ComposeView |
| 布局控制 | 有限（SDK 控制） | 完全（你控制周围 UI） |
| 初始化 | 启动 activity | Suspend 函数 |
| 回调 | `AirwallexCheckoutListener` | `PaymentFlowListener` 或 lambda |

---

## 低层 API 集成

你可以基于低层 API 完全自定义 UI。

### 步骤 1：SDK 安装

SDK 支持 Android API 21 及以上。

在 app 级 `build.gradle` 添加依赖：

```groovy
dependencies {
    // 必须
    implementation 'io.github.airwallex:payment-components-core:6.4.2'
    // 按需添加支付方式
    implementation 'io.github.airwallex:payment-card:6.4.2'
    implementation 'io.github.airwallex:payment-googlepay:6.4.2'
    implementation 'io.github.airwallex:payment-redirect:6.4.2'
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
https://www.airwallex.com/docs/payments/test-and-go-live/test-card-numbers

---

## 贡献

欢迎任何形式的贡献，包括新功能、bug修复和文档改进。最简单的方式是创建 pull request，我们会尽快回复。如果你发现任何错误或有建议，欢迎提交 issue。

---

如需 Markdown 源文件，可直接复制上方内容到 GUIDE-zh.md。
