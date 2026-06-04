//[googlepay](../../../index.md)/[com.airwallex.android.googlepay](../index.md)/[GooglePayComponent](index.md)

# GooglePayComponent

[androidJvm]\
class [GooglePayComponent](index.md) : [ActionComponent](../../../../components-core/components-core/com.airwallex.android.core/-action-component/index.md)

## Constructors

| | |
|---|---|
| [GooglePayComponent](-google-pay-component.md) | [androidJvm]<br>constructor() |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Properties

| Name | Summary |
|---|---|
| [paymentMethodType](payment-method-type.md) | [androidJvm]<br>lateinit var [paymentMethodType](payment-method-type.md): [AvailablePaymentMethodType](../../../../components-core/components-core/com.airwallex.android.core.model/-available-payment-method-type/index.md) |
| [session](session.md) | [androidJvm]<br>lateinit var [session](session.md): [AirwallexSession](../../../../components-core/components-core/com.airwallex.android.core/-airwallex-session/index.md) |

## Functions

| Name | Summary |
|---|---|
| [handleActivityResult](handle-activity-result.md) | [androidJvm]<br>open override fun [handleActivityResult](handle-activity-result.md)(requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), resultCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), data: [Intent](https://developer.android.com/reference/kotlin/android/content/Intent.html)?, listener: [Airwallex.PaymentResultListener](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [handlePaymentData](index.md#-1330867726%2FFunctions%2F698761288) | [androidJvm]<br>open fun &lt;[T](index.md#-1330867726%2FFunctions%2F698761288), [R](index.md#-1330867726%2FFunctions%2F698761288)&gt; [handlePaymentData](index.md#-1330867726%2FFunctions%2F698761288)(param: [T](index.md#-1330867726%2FFunctions%2F698761288)?, callBack: (result: [R](index.md#-1330867726%2FFunctions%2F698761288)?) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)) |
| [handlePaymentIntentResponse](handle-payment-intent-response.md) | [androidJvm]<br>open override fun [handlePaymentIntentResponse](handle-payment-intent-response.md)(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, nextAction: [NextAction](../../../../components-core/components-core/com.airwallex.android.core.model/-next-action/index.md)?, fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)?, activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), applicationContext: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), cardNextActionModel: [CardNextActionModel](../../../../components-core/components-core/com.airwallex.android.core/-card-next-action-model/index.md)?, listener: [Airwallex.PaymentResultListener](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md), consentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |
| [initialize](initialize.md) | [androidJvm]<br>open override fun [initialize](initialize.md)(application: [Application](https://developer.android.com/reference/kotlin/android/app/Application.html)) |
