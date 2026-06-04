//[redirect](../../../index.md)/[com.airwallex.android.redirect](../index.md)/[RedirectComponent](index.md)

# RedirectComponent

[androidJvm]\
class [RedirectComponent](index.md) : [ActionComponent](../../../../components-core/components-core/com.airwallex.android.core/-action-component/index.md)

## Constructors

| | |
|---|---|
| [RedirectComponent](-redirect-component.md) | [androidJvm]<br>constructor() |

## Types

| Name | Summary |
|---|---|
| [Companion](-companion/index.md) | [androidJvm]<br>object [Companion](-companion/index.md) |

## Functions

| Name | Summary |
|---|---|
| [handleActivityResult](handle-activity-result.md) | [androidJvm]<br>open override fun [handleActivityResult](handle-activity-result.md)(requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), resultCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), data: [Intent](https://developer.android.com/reference/kotlin/android/content/Intent.html)?, listener: [Airwallex.PaymentResultListener](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md)?): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [handlePaymentData](index.md#-1330867726%2FFunctions%2F319035559) | [androidJvm]<br>open fun &lt;[T](index.md#-1330867726%2FFunctions%2F319035559), [R](index.md#-1330867726%2FFunctions%2F319035559)&gt; [handlePaymentData](index.md#-1330867726%2FFunctions%2F319035559)(param: [T](index.md#-1330867726%2FFunctions%2F319035559)?, callBack: (result: [R](index.md#-1330867726%2FFunctions%2F319035559)?) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)) |
| [handlePaymentIntentResponse](handle-payment-intent-response.md) | [androidJvm]<br>open override fun [handlePaymentIntentResponse](handle-payment-intent-response.md)(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, nextAction: [NextAction](../../../../components-core/components-core/com.airwallex.android.core.model/-next-action/index.md)?, fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)?, activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), applicationContext: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), cardNextActionModel: [CardNextActionModel](../../../../components-core/components-core/com.airwallex.android.core/-card-next-action-model/index.md)?, listener: [Airwallex.PaymentResultListener](../../../../components-core/components-core/com.airwallex.android.core/-airwallex/-payment-result-listener/index.md), consentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?) |
| [initialize](index.md#-1136983818%2FFunctions%2F319035559) | [androidJvm]<br>open fun [initialize](index.md#-1136983818%2FFunctions%2F319035559)(application: [Application](https://developer.android.com/reference/kotlin/android/app/Application.html)) |
