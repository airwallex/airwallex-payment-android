//[components-core](../../../../index.md)/[com.airwallex.android.core.model](../../index.md)/[PaymentIntent](../index.md)/[PaymentAttempt](index.md)

# PaymentAttempt

[androidJvm]\
data class [PaymentAttempt](index.md)(val id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, val amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?, val currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val paymentMethod: [PaymentMethod](../../-payment-method/index.md), val capturedAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?, val refundedAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?, val createdAt: [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)?, val updatedAt: [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)?, val authenticationData: [PaymentIntent.PaymentAttemptAuthData](../-payment-attempt-auth-data/index.md)?, val status: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, val failureDetails: [PaymentIntent.FailureDetails](../-failure-details/index.md)? = null) : [AirwallexModel](../../-airwallex-model/index.md), [Parcelable](https://developer.android.com/reference/kotlin/android/os/Parcelable.html)

## Constructors

| | |
|---|---|
| [PaymentAttempt](-payment-attempt.md) | [androidJvm]<br>constructor(id: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?, amount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?, currency: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, paymentMethod: [PaymentMethod](../../-payment-method/index.md), capturedAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?, refundedAmount: [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?, createdAt: [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)?, updatedAt: [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)?, authenticationData: [PaymentIntent.PaymentAttemptAuthData](../-payment-attempt-auth-data/index.md)?, status: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null, failureDetails: [PaymentIntent.FailureDetails](../-failure-details/index.md)? = null) |

## Properties

| Name | Summary |
|---|---|
| [amount](amount.md) | [androidJvm]<br>val [amount](amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?<br>Payment amount. This is the order amount you would like to charge your customer |
| [authenticationData](authentication-data.md) | [androidJvm]<br>val [authenticationData](authentication-data.md): [PaymentIntent.PaymentAttemptAuthData](../-payment-attempt-auth-data/index.md)?<br>Authentication data used by the payment attempt |
| [capturedAmount](captured-amount.md) | [androidJvm]<br>val [capturedAmount](captured-amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?<br>Captured amount |
| [createdAt](created-at.md) | [androidJvm]<br>val [createdAt](created-at.md): [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)?<br>Time at which this payment attempt was created |
| [currency](currency.md) | [androidJvm]<br>val [currency](currency.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Currency of the captured and refunded amounts |
| [failureDetails](failure-details.md) | [androidJvm]<br>val [failureDetails](failure-details.md): [PaymentIntent.FailureDetails](../-failure-details/index.md)? = null<br>Failure details if the payment attempt failed |
| [id](id.md) | [androidJvm]<br>val [id](id.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)?<br>Unique identifier for the payment attempt |
| [paymentMethod](payment-method.md) | [androidJvm]<br>val [paymentMethod](payment-method.md): [PaymentMethod](../../-payment-method/index.md)<br>Payment method used by the payment attempt |
| [refundedAmount](refunded-amount.md) | [androidJvm]<br>val [refundedAmount](refunded-amount.md): [BigDecimal](https://developer.android.com/reference/kotlin/java/math/BigDecimal.html)?<br>Refunded amount |
| [status](status.md) | [androidJvm]<br>val [status](status.md): [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html)? = null<br>Status of the payment attempt |
| [updatedAt](updated-at.md) | [androidJvm]<br>val [updatedAt](updated-at.md): [Date](https://developer.android.com/reference/kotlin/java/util/Date.html)?<br>Last time at which this payment attempt was updated or operated on |

## Functions

| Name | Summary |
|---|---|
| [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [describeContents](../../-we-chat/index.md#-1578325224%2FFunctions%2F1424399983)(): [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html) |
| [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983) | [androidJvm]<br>abstract fun [writeToParcel](../../-we-chat/index.md#-1754457655%2FFunctions%2F1424399983)(p0: [Parcel](https://developer.android.com/reference/kotlin/android/os/Parcel.html), p1: [Int](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-int/index.html)) |
