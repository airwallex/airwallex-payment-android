//[airwallex](../../../../index.md)/[com.airwallex.android.view](../../index.md)/[PaymentFlowViewModel](../index.md)/[DeleteConsentResult](index.md)

# DeleteConsentResult

sealed class [DeleteConsentResult](index.md)

Result of deleting a payment consent

#### Inheritors

| |
|---|
| [Success](-success/index.md) |
| [Failure](-failure/index.md) |

## Types

| Name | Summary |
|---|---|
| [Failure](-failure/index.md) | [androidJvm]<br>data class [Failure](-failure/index.md)(val exception: [Throwable](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-throwable/index.html)) : [PaymentFlowViewModel.DeleteConsentResult](index.md) |
| [Success](-success/index.md) | [androidJvm]<br>data class [Success](-success/index.md)(val consent: [PaymentConsent](../../../../../components-core/components-core/com.airwallex.android.core.model/-payment-consent/index.md)) : [PaymentFlowViewModel.DeleteConsentResult](index.md) |
