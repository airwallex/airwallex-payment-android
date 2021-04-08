package com.airwallex.android.view

import com.airwallex.android.Airwallex
import com.airwallex.android.exception.InvalidParamsException
import com.airwallex.android.model.*
import java.math.BigDecimal

internal abstract class AirwallexCheckoutBaseActivity : AirwallexActivity() {

    companion object {
        private const val RETURN_URL = "https://www.airwallex.com"
    }

    abstract val airwallex: Airwallex

    abstract val paymentIntent: PaymentIntent

    abstract val cvc: String?

    override fun onActionSave() {
        // Ignore
    }

    private fun createPaymentConsent(
        paymentMethod: PaymentMethod,
        nextTriggeredBy: PaymentConsent.NextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
        merchantTriggerReason: PaymentConsent.MerchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
        requiresCvc: Boolean = false,
        listener: Airwallex.PaymentListener<PaymentConsent>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        val params: CreatePaymentConsentParams = when (requireNotNull(paymentMethod.type)) {
            PaymentMethodType.ALIPAY_HK -> {
                CreatePaymentConsentParams.createAlipayHKParams(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = requireNotNull(paymentIntent.customerId),
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.DANA -> {
                CreatePaymentConsentParams.createDanaParams(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = requireNotNull(paymentIntent.customerId),
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.GCASH -> {
                CreatePaymentConsentParams.createGCashParams(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = requireNotNull(paymentIntent.customerId),
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.KAKAOPAY -> {
                CreatePaymentConsentParams.createKakaoParams(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = requireNotNull(paymentIntent.customerId),
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.TNG -> {
                CreatePaymentConsentParams.createTngParams(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = requireNotNull(paymentIntent.customerId),
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.CARD -> {
                if (requiresCvc && nextTriggeredBy == PaymentConsent.NextTriggeredBy.MERCHANT) {
                    listener.onFailed(InvalidParamsException(message = "Only applicable when next_triggered_by is customer and the payment_method.type is card"))
                    return
                }
                CreatePaymentConsentParams.createCardParams(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = requireNotNull(paymentIntent.customerId),
                    paymentMethodId = requireNotNull(paymentMethod.id),
                    nextTriggeredBy = nextTriggeredBy,
                    merchantTriggerReason = merchantTriggerReason,
                    requiresCvc = requiresCvc
                )
            }
            else -> {
                listener.onFailed(InvalidParamsException(message = "Not support payment method ${paymentMethod.type} when creating payment consent"))
                return
            }
        }

        airwallex.createPaymentConsent(
            params = params,
            listener = object : Airwallex.PaymentListener<PaymentConsent> {
                override fun onFailed(exception: Exception) {
                    listener.onFailed(exception)
                }

                override fun onSuccess(response: PaymentConsent) {
                    listener.onSuccess(response)
                }
            }
        )
    }

    private fun verifyPaymentConsent(
        paymentConsent: PaymentConsent,
        amount: BigDecimal? = null,
        cvc: String? = null,
        listener: Airwallex.PaymentListener<PaymentConsent>
    ) {
        if (paymentConsent.requiresCvc && cvc == null) {
            listener.onFailed(InvalidParamsException(message = "CVC is required!"))
        }
        val params: VerifyPaymentConsentParams = when (requireNotNull(paymentConsent.paymentMethod?.type)) {
            PaymentMethodType.ALIPAY_HK -> {
                VerifyPaymentConsentParams.createAlipayHKParams(
                    clientSecret = requireNotNull(paymentConsent.clientSecret),
                    paymentConsentId = requireNotNull(paymentConsent.id)
                )
            }
            PaymentMethodType.DANA -> {
                VerifyPaymentConsentParams.createDanaParams(
                    clientSecret = requireNotNull(paymentConsent.clientSecret),
                    paymentConsentId = requireNotNull(paymentConsent.id)
                )
            }
            PaymentMethodType.GCASH -> {
                VerifyPaymentConsentParams.createGCashParams(
                    clientSecret = requireNotNull(paymentConsent.clientSecret),
                    paymentConsentId = requireNotNull(paymentConsent.id)
                )
            }
            PaymentMethodType.KAKAOPAY -> {
                VerifyPaymentConsentParams.createKakaoParams(
                    clientSecret = requireNotNull(paymentConsent.clientSecret),
                    paymentConsentId = requireNotNull(paymentConsent.id)
                )
            }
            PaymentMethodType.TNG -> {
                VerifyPaymentConsentParams.createTngParams(
                    clientSecret = requireNotNull(paymentConsent.clientSecret),
                    paymentConsentId = requireNotNull(paymentConsent.id)
                )
            }
            PaymentMethodType.CARD -> {
                VerifyPaymentConsentParams.createCardParams(
                    clientSecret = requireNotNull(paymentConsent.clientSecret),
                    paymentConsentId = requireNotNull(paymentConsent.id),
                    amount = amount,
                    currency = requireNotNull(paymentIntent.currency),
                    cvc = cvc,
                    returnUrl = RETURN_URL
                )
            }
            else -> {
                throw InvalidParamsException(message = "Not support payment method ${paymentConsent.paymentMethod?.type} when verifying payment consent")
            }
        }

        airwallex.verifyPaymentConsent(
            params = params,
            listener = object : Airwallex.PaymentListener<PaymentConsent> {
                override fun onFailed(exception: Exception) {
                    listener.onFailed(exception)
                }

                override fun onSuccess(response: PaymentConsent) {
                    listener.onSuccess(response)
                }
            }
        )
    }

    internal fun createAndVerifyPaymentConsent(paymentMethod: PaymentMethod, listener: Airwallex.PaymentListener<PaymentConsent>) {
        createPaymentConsent(
            paymentMethod = paymentMethod,
            listener = object : Airwallex.PaymentListener<PaymentConsent> {
                override fun onFailed(exception: Exception) {
                    listener.onFailed(exception)
                }

                override fun onSuccess(response: PaymentConsent) {
                    verifyPaymentConsent(
                        paymentConsent = response,
                        listener = object : Airwallex.PaymentListener<PaymentConsent> {
                            override fun onFailed(exception: Exception) {
                                listener.onFailed(exception)
                            }

                            override fun onSuccess(response: PaymentConsent) {
                                listener.onSuccess(response)
                            }
                        }
                    )
                }
            }
        )
    }

    internal fun confirmPaymentIntent(
        paymentMethod: PaymentMethod,
        paymentConsent: PaymentConsent? = null,
        listener: Airwallex.PaymentListener<PaymentIntent>
    ) {
        setLoadingProgress(loading = true, cancelable = false)
        val params = when (requireNotNull(paymentMethod.type)) {
            PaymentMethodType.WECHAT -> {
                ConfirmPaymentIntentParams.createWeChatParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsent?.id
                )
            }
            PaymentMethodType.ALIPAY_CN -> {
                ConfirmPaymentIntentParams.createAlipayParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsent?.id
                )
            }
            PaymentMethodType.ALIPAY_HK -> {
                ConfirmPaymentIntentParams.createAlipayHKParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsent?.id
                )
            }
            PaymentMethodType.DANA -> {
                ConfirmPaymentIntentParams.createDanaParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsent?.id
                )
            }
            PaymentMethodType.GCASH -> {
                ConfirmPaymentIntentParams.createGCashParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsent?.id
                )
            }
            PaymentMethodType.KAKAOPAY -> {
                ConfirmPaymentIntentParams.createKakaoParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsent?.id
                )
            }
            PaymentMethodType.TNG -> {
                ConfirmPaymentIntentParams.createTngParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsent?.id
                )
            }
            PaymentMethodType.CARD -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    paymentMethodId = requireNotNull(paymentMethod.id),
                    cvc = requireNotNull(cvc),
                    customerId = paymentIntent.customerId,
                    paymentConsentId = paymentConsent?.id
                )
            }
        }
        airwallex.confirmPaymentIntent(params, listener)
    }
}
