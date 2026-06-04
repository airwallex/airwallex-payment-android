//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[ActionComponent](index.md)

# ActionComponent

[androidJvm]\
interface [ActionComponent](index.md)

## Functions

| Name | Summary |
|---|---|
| [handleActivityResult](handle-activity-result.md) | [androidJvm]<br>abstract fun [handleActivityResult](handle-activity-result.md)(requestCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), resultCode: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html), data: [Intent](https://developer.android.com/reference/kotlin/android/content/Intent.html)?, listener: [Airwallex.PaymentResultListener](../-airwallex/-payment-result-listener/index.md)? = null): [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) |
| [handlePaymentData](handle-payment-data.md) | [androidJvm]<br>open fun &lt;[T](handle-payment-data.md), [R](handle-payment-data.md)&gt; [handlePaymentData](handle-payment-data.md)(param: [T](handle-payment-data.md)?, callBack: (result: [R](handle-payment-data.md)?) -&gt; [Unit](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-unit/index.html)) |
| [handlePaymentIntentResponse](handle-payment-intent-response.md) | [androidJvm]<br>abstract fun [handlePaymentIntentResponse](handle-payment-intent-response.md)(paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, nextAction: [NextAction](../../com.airwallex.android.core.model/-next-action/index.md)?, fragment: [Fragment](https://developer.android.com/reference/kotlin/androidx/fragment/app/Fragment.html)? = null, activity: [Activity](https://developer.android.com/reference/kotlin/android/app/Activity.html), applicationContext: [Context](https://developer.android.com/reference/kotlin/android/content/Context.html), cardNextActionModel: [CardNextActionModel](../-card-next-action-model/index.md)?, listener: [Airwallex.PaymentResultListener](../-airwallex/-payment-result-listener/index.md), consentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null) |
| [initialize](initialize.md) | [androidJvm]<br>open fun [initialize](initialize.md)(application: [Application](https://developer.android.com/reference/kotlin/android/app/Application.html)) |
