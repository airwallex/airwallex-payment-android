//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[CardNextActionModel](index.md)/[CardNextActionModel](-card-next-action-model.md)

# CardNextActionModel

[androidJvm]\
constructor(paymentManager: [PaymentManager](../-payment-manager/index.md), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), device: [Device](../../com.airwallex.android.core.model/-device/index.md)?, paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html), activityProvider: () -&gt; [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/core/app/ComponentActivity.html))

#### Parameters

androidJvm

| | |
|---|---|
| paymentManager | Manager for handling payment operations |
| clientSecret | Client secret for the payment intent |
| device | Device information for fingerprinting |
| paymentIntentId | ID of the payment intent being processed |
| currency | Currency code for the transaction |
| amount | Transaction amount |
| activityProvider | Lambda function that provides the current activity reference.     This is crucial for handling configuration changes (e.g., screen rotation).     Instead of capturing a static activity reference that becomes stale after     configuration changes, this provider is called dynamically to always get     the current, valid activity instance. This ensures that activities launched     during async operations (like ThreeDSecurityActivity) use the correct activity     context even after multiple screen rotations. |
