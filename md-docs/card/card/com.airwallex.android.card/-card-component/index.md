//[card](../../../index.md)/[com.airwallex.android.card](../index.md)/[CardComponent](index.md)

# CardComponent

[androidJvm]\
class [CardComponent](index.md) : [ActionComponent](../../../../components-core/components-core/com.airwallex.android.core/-action-component/index.md)

## Constructors

| | |
|---|---|
| [CardComponent](-card-component.md) | [androidJvm]<br>constructor() |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [handleActivityResult](handle-activity-result.md) | [androidJvm]<br>open override fun [handleActivityResult](handle-activity-result.md)(requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), resultCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), data: [Intent](https://developer.android.com/reference/kotlin/android/content/Intent.html)?, listener: [Airwallex.PaymentResultListener](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [handlePaymentData](handle-payment-data.md) | [androidJvm]<br>open override fun &lt;[T](handle-payment-data.md), [R](handle-payment-data.md)&gt; [handlePaymentData](handle-payment-data.md)(param: [T](handle-payment-data.md)?, callBack: (result: [R](handle-payment-data.md)?) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)) |
| [handlePaymentIntentResponse](handle-payment-intent-response.md) | [androidJvm]<br>open override fun [handlePaymentIntentResponse](handle-payment-intent-response.md)(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, nextAction: [NextAction](../../../../components-core/components-core/com.airwallex.android.core.model/-next-action/index.md)?, fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)?, activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), applicationContext: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), cardNextActionModel: [CardNextActionModel](../../../../components-core/components-core/com.airwallex.android.core/-card-next-action-model/index.md)?, listener: [Airwallex.PaymentResultListener](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md), consentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |
| [initialize](initialize.md) | [androidJvm]<br>open override fun [initialize](initialize.md)(application: [Application](https://developer.android.com/reference/kotlin/android/app/Application.html)) |
