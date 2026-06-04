//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[confirmPaymentIntent](confirm-payment-intent.md)

# confirmPaymentIntent

[androidJvm]\

@[UiThread](https://developer.android.com/reference/kotlin/androidx/annotation/UiThread.html)

fun [~~confirmPaymentIntent~~](confirm-payment-intent.md)(session: [AirwallexSession](../-airwallex-session/index.md), card: [PaymentMethod.Card](../../com.airwallex.android.core.model/-payment-method/-card/index.md), billing: [Billing](../../com.airwallex.android.core.model/-billing/index.md)?, saveCard: [Boolean](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-boolean/index.html) = false, listener: [Airwallex.PaymentResultListener](-payment-result-listener/index.md))

---

### Deprecated

Use checkout() with PaymentMethod instead

#### Replace with

```kotlin
import com.airwallex.android.core.model.PaymentMethod

```
```kotlin
checkout(session, paymentMethod, cvc = card.cvc, saveCard = saveCard, listener = listener)
```
---

Confirm a payment intent with card and billing details

#### Parameters

androidJvm

| | |
|---|---|
| session | a [AirwallexSession](../-airwallex-session/index.md) used to start the payment flow |
| card | the card information |
| billing | the billing information, it's optional |
| saveCard | whether card will be saved as a payment consent, if set as true, [AirwallexSession.customerId](../-airwallex-session/customer-id.md) must be provided for the [session](confirm-payment-intent.md) |
| listener | The callback of the payment flow |

[androidJvm]\

@[UiThread](https://developer.android.com/reference/kotlin/androidx/annotation/UiThread.html)

fun [~~confirmPaymentIntent~~](confirm-payment-intent.md)(session: [AirwallexSession](../-airwallex-session/index.md), paymentConsent: [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md), listener: [Airwallex.PaymentResultListener](-payment-result-listener/index.md))

---

### Deprecated

Use checkout() instead

#### Replace with

```kotlin
checkout(session, paymentConsent.paymentMethod!!, paymentConsentId = paymentConsent.id, paymentConsent = paymentConsent, listener = listener)
```
---

Confirm a payment intent with payment consent

#### Parameters

androidJvm

| | |
|---|---|
| session | an [AirwallexSession](../-airwallex-session/index.md) used to start the payment flow |
| paymentConsent | a [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md) used to start the payment flow |
| listener | The callback of the payment flow |

[androidJvm]\
fun [~~confirmPaymentIntent~~](confirm-payment-intent.md)(session: [AirwallexSession](../-airwallex-session/index.md), paymentConsentId: [String](https://kotlinlang.org/api/core/kotlin-stdlib/kotlin/-string/index.html), listener: [Airwallex.PaymentResultListener](-payment-result-listener/index.md))

---

### Deprecated

Use checkout() instead

#### Replace with

```kotlin
import com.airwallex.android.core.model.PaymentMethod

```
```kotlin
checkout(session, PaymentMethod(type = "card"), paymentConsentId = paymentConsentId, listener = listener)
```
---

Confirm a payment intent with payment consent ID

#### Parameters

androidJvm

| | |
|---|---|
| session | an [AirwallexSession](../-airwallex-session/index.md) used to start the payment flow |
| paymentConsentId | the ID of the [PaymentConsent](../../com.airwallex.android.core.model/-payment-consent/index.md) |
| listener | The callback of the payment flow |
