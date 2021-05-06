package com.airwallex.paymentacceptance

import androidx.fragment.app.Fragment
import com.airwallex.android.*
import com.airwallex.android.exception.InvalidParamsException
import com.airwallex.android.model.*
import java.math.BigDecimal

open class BasePaymentCartFragment : Fragment() {

    internal val airwallex by lazy {
        Airwallex(this)
    }

    private fun createPaymentConsent(
        clientSecret: String,
        customerId: String,
        paymentMethod: PaymentMethod,
        nextTriggeredBy: PaymentConsent.NextTriggeredBy = PaymentConsent.NextTriggeredBy.MERCHANT,
        merchantTriggerReason: PaymentConsent.MerchantTriggerReason = PaymentConsent.MerchantTriggerReason.UNSCHEDULED,
        requiresCvc: Boolean = false,
        listener: Airwallex.PaymentListener<PaymentConsent>
    ) {
        val params: CreatePaymentConsentParams = when (requireNotNull(paymentMethod.type)) {
            PaymentMethodType.ALIPAY_HK -> {
                CreatePaymentConsentParams.createAlipayHKParams(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.DANA -> {
                CreatePaymentConsentParams.createDanaParams(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.GCASH -> {
                CreatePaymentConsentParams.createGCashParams(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.KAKAOPAY -> {
                CreatePaymentConsentParams.createKakaoParams(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.TNG -> {
                CreatePaymentConsentParams.createTngParams(
                    clientSecret = clientSecret,
                    customerId = customerId,
                    merchantTriggerReason = merchantTriggerReason
                )
            }
            PaymentMethodType.CARD -> {
                if (requiresCvc && nextTriggeredBy == PaymentConsent.NextTriggeredBy.MERCHANT) {
                    listener.onFailed(InvalidParamsException(message = "Only applicable when next_triggered_by is customer and the payment_method.type is card"))
                    return
                }
                CreatePaymentConsentParams.createCardParams(
                    clientSecret = clientSecret,
                    customerId = customerId,
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
        airwallex.createPaymentConsent(params, listener)
    }

    private fun verifyPaymentConsent(
        paymentConsent: PaymentConsent,
        currency: String,
        amount: BigDecimal? = null,
        cvc: String? = null,
        listener: Airwallex.PaymentResultListener<PaymentIntent>
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
                    currency = currency,
                    cvc = cvc,
                    returnUrl = "airwallexcheckout://${requireContext().packageName}"
                )
            }
            else -> {
                throw InvalidParamsException(message = "Not support payment method ${paymentConsent.paymentMethod?.type} when verifying payment consent")
            }
        }

        airwallex.verifyPaymentConsent(params, listener)
    }

    private fun confirmPaymentIntent(
        paymentIntentId: String,
        clientSecret: String,
        paymentMethod: PaymentMethod,
        cvc: String? = null,
        customerId: String? = null,
        paymentConsentId: String? = null,
        listener: Airwallex.PaymentResultListener<PaymentIntent>
    ) {
        val params = when (requireNotNull(paymentMethod.type)) {
            PaymentMethodType.WECHAT -> {
                ConfirmPaymentIntentParams.createWeChatParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            PaymentMethodType.ALIPAY_CN -> {
                ConfirmPaymentIntentParams.createAlipayParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            PaymentMethodType.ALIPAY_HK -> {
                ConfirmPaymentIntentParams.createAlipayHKParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            PaymentMethodType.DANA -> {
                ConfirmPaymentIntentParams.createDanaParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            PaymentMethodType.GCASH -> {
                ConfirmPaymentIntentParams.createGCashParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            PaymentMethodType.KAKAOPAY -> {
                ConfirmPaymentIntentParams.createKakaoParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            PaymentMethodType.TNG -> {
                ConfirmPaymentIntentParams.createTngParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            PaymentMethodType.CARD -> {
                ConfirmPaymentIntentParams.createCardParams(
                    paymentIntentId = paymentIntentId,
                    clientSecret = clientSecret,
                    paymentMethodId = requireNotNull(paymentMethod.id),
                    cvc = requireNotNull(cvc),
                    customerId = customerId,
                    paymentConsentId = paymentConsentId
                )
            }
            else -> {
                listener.onFailed(InvalidParamsException("Not support payment method type ${paymentMethod.type}"))
                return
            }
        }
        airwallex.confirmPaymentIntent(params, listener)
    }

    internal fun startCheckout(session: AirwallexSession, paymentMethod: PaymentMethod, cvc: String? = null, listener: Airwallex.PaymentResultListener<PaymentIntent>) {
        when (session) {
            is AirwallexPaymentSession -> {
                val paymentIntent = session.paymentIntent
                confirmPaymentIntent(
                    paymentIntentId = paymentIntent.id,
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    paymentMethod = paymentMethod,
                    cvc = cvc,
                    customerId = paymentIntent.customerId,
                    listener = listener
                )
            }
            is AirwallexRecurringSession -> {
                val customerId = requireNotNull(session.customerId)
                ClientSecretRepository.getInstance().retrieveClientSecret(
                    customerId,
                    object : ClientSecretRepository.ClientSecretRetrieveListener {
                        override fun onClientSecretRetrieve(clientSecret: ClientSecret) {
                            createPaymentConsent(
                                clientSecret = clientSecret.value,
                                customerId = customerId,
                                paymentMethod = paymentMethod,
                                nextTriggeredBy = session.nextTriggerBy,
                                listener = object : Airwallex.PaymentListener<PaymentConsent> {
                                    override fun onFailed(exception: Exception) {
                                        listener.onFailed(exception)
                                    }

                                    override fun onSuccess(response: PaymentConsent) {
                                        verifyPaymentConsent(
                                            paymentConsent = response,
                                            currency = session.currency,
                                            amount = session.amount,
                                            cvc = session.cvc,
                                            listener = listener
                                        )
                                    }
                                }
                            )
                        }

                        override fun onClientSecretError(errorMessage: String) {
                            listener.onFailed(Exception(errorMessage))
                        }
                    }
                )
            }
            is AirwallexRecurringWithIntentSession -> {
                val paymentIntent = session.paymentIntent
                createPaymentConsent(
                    clientSecret = requireNotNull(paymentIntent.clientSecret),
                    customerId = requireNotNull(paymentIntent.customerId),
                    paymentMethod = paymentMethod,
                    nextTriggeredBy = session.nextTriggerBy,
                    listener = object : Airwallex.PaymentListener<PaymentConsent> {
                        override fun onFailed(exception: Exception) {
                            listener.onFailed(exception)
                        }

                        override fun onSuccess(response: PaymentConsent) {
                            confirmPaymentIntent(
                                paymentIntentId = paymentIntent.id,
                                clientSecret = requireNotNull(paymentIntent.clientSecret),
                                paymentMethod = paymentMethod,
                                cvc = cvc,
                                customerId = paymentIntent.customerId,
                                paymentConsentId = response.id,
                                listener = listener
                            )
                        }
                    }
                )
            }
        }
    }
}
