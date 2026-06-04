//[components-core](../../../index.md)/[com.airwallex.android.core.model](../index.md)/[VerifyPaymentConsentParams](index.md)/[amount](amount.md)

# amount

[androidJvm]\
val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)? = null

The alternative amount of verification if zero amount is not acceptable for the provider. The transaction of this amount should be reverted once the verification process finished. Must be greater than 0.
