//[components-core](../../../index.md)/[com.airwallex.android.core](../index.md)/[Airwallex](index.md)/[confirmPaymentIntentWithDevice](confirm-payment-intent-with-device.md)

# confirmPaymentIntentWithDevice

[androidJvm]\
fun [~~confirmPaymentIntentWithDevice~~](confirm-payment-intent-with-device.md)(device: [Device](../../com.airwallex.android.core.model/-device/index.md)? = null, params: [ConfirmPaymentIntentParams](../../com.airwallex.android.core.model/-confirm-payment-intent-params/index.md), listener: [Airwallex.PaymentResultListener](-payment-result-listener/index.md))

---

### Deprecated

Use checkout() for standard payment flows. This low-level API is kept for advanced device fingerprinting only.

---

Confirm PaymentIntent with Device Fingerprinting

#### Parameters

androidJvm

| | |
|---|---|
| device | a [Device](../../com.airwallex.android.core.model/-device/index.md) object containing device information for fingerprinting, optional. |
| params | [ConfirmPaymentIntentParams](../../com.airwallex.android.core.model/-confirm-payment-intent-params/index.md) used to confirm the payment intent |
| listener | a [PaymentResultListener](-payment-result-listener/index.md) to receive the response or error |
