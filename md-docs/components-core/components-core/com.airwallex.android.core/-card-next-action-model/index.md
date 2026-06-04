//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[CardNextActionModel](index.md)

# CardNextActionModel

class [CardNextActionModel](index.md)(val paymentManager: [PaymentManager](../-payment-manager/index.md), val clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val device: [Device](../../com.airwallex.android.core.model/-device/index.md)?, val paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), val amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html), val activityProvider: () -&gt; [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/core/app/ComponentActivity.html))

Model containing necessary information for handling card payment next actions, particularly for 3DS authentication flows.

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

## Constructors

| | |
|---|---|
| [CardNextActionModel](-card-next-action-model.md) | [androidJvm]<br>constructor(paymentManager: [PaymentManager](../-payment-manager/index.md), clientSecret: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), device: [Device](../../com.airwallex.android.core.model/-device/index.md)?, paymentIntentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html), activityProvider: () -&gt; [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/core/app/ComponentActivity.html)) |

## Properties

| Name | Summary |
|---|---|
| [activityProvider](activity-provider.md) | [androidJvm]<br>val [activityProvider](activity-provider.md): () -&gt; [ComponentActivity](https://developer.android.com/reference/kotlin/androidx/core/app/ComponentActivity.html) |
| [amount](amount.md) | [androidJvm]<br>val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html) |
| [clientSecret](client-secret.md) | [androidJvm]<br>val [clientSecret](client-secret.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [currency](currency.md) | [androidJvm]<br>val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html) |
| [device](device.md) | [androidJvm]<br>val [device](device.md): [Device](../../com.airwallex.android.core.model/-device/index.md)? |
| [paymentIntentId](payment-intent-id.md) | [androidJvm]<br>val [paymentIntentId](payment-intent-id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? |
| [paymentManager](payment-manager.md) | [androidJvm]<br>val [paymentManager](payment-manager.md): [PaymentManager](../-payment-manager/index.md) |
