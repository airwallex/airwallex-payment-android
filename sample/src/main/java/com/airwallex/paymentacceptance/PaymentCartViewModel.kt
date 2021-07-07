package com.airwallex.paymentacceptance

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.*
import com.airwallex.android.model.*

internal class PaymentCartViewModel(
    application: Application,
    private val airwallex: Airwallex
) : AndroidViewModel(application) {

    fun presentSelectPaymentMethodFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider
    ): LiveData<PaymentMethodResult> {
        val resultData = MutableLiveData<PaymentMethodResult>()
        airwallex.presentSelectPaymentMethodFlow(
            session,
            clientSecretProvider,
            object : Airwallex.PaymentMethodListener {
                override fun onSuccess(
                    paymentMethod: PaymentMethod,
                    paymentConsentId: String?,
                    cvc: String?
                ) {
                    resultData.value = PaymentMethodResult.Success(paymentMethod, paymentConsentId, cvc)
                }

                override fun onFailed(error: Exception) {
                    resultData.value = PaymentMethodResult.Error(error)
                }

                override fun onCancelled() {
                    resultData.value = PaymentMethodResult.Cancel
                }
            }
        )
        return resultData
    }

    fun presentShippingFlow(shipping: Shipping?): LiveData<ShippingResult> {
        val resultData = MutableLiveData<ShippingResult>()
        airwallex.presentShippingFlow(
            shipping,
            object : Airwallex.PaymentShippingListener {
                override fun onSuccess(shipping: Shipping) {
                    resultData.value = ShippingResult.Success(shipping)
                }

                override fun onCancelled() {
                    resultData.value = ShippingResult.Cancel
                }
            }
        )
        return resultData
    }

    fun presentPaymentFlow(
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider? = null
    ): LiveData<PaymentFlowResult> {
        val resultData = MutableLiveData<PaymentFlowResult>()
        airwallex.presentPaymentFlow(
            session,
            clientSecretProvider,
            object : Airwallex.PaymentIntentListener {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    resultData.value = PaymentFlowResult.Success(paymentIntent)
                }

                override fun onFailed(error: Exception) {
                    resultData.value = PaymentFlowResult.Error(error)
                }

                override fun onNextActionWithWeChatPay(weChat: WeChat) {
                    resultData.value = PaymentFlowResult.WeChatPay(weChat)
                }

                override fun onNextActionWithRedirectUrl(url: String) {
                    resultData.value = PaymentFlowResult.Redirect(url)
                }

                override fun onCancelled() {
                    resultData.value = PaymentFlowResult.Cancel
                }
            }
        )
        return resultData
    }

    fun checkout(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String?,
        cvc: String?,
    ): LiveData<PaymentCheckoutResult> {
        val resultData = MutableLiveData<PaymentCheckoutResult>()
        airwallex.checkout(
            session = session,
            paymentMethod = paymentMethod,
            paymentConsentId = paymentConsentId,
            cvc = cvc,
            listener = object : Airwallex.PaymentResultListener<PaymentIntent> {
                override fun onFailed(exception: Exception) {
                    resultData.value = PaymentCheckoutResult.Error(exception)
                }

                override fun onSuccess(response: PaymentIntent) {
                    resultData.value = PaymentCheckoutResult.Success(response)
                }

                override fun onNextActionWithWeChatPay(weChat: WeChat) {
                    resultData.value = PaymentCheckoutResult.WeChatPay(weChat)
                }

                override fun onNextActionWithRedirectUrl(url: String) {
                    resultData.value = PaymentCheckoutResult.Redirect(url)
                }
            }
        )
        return resultData
    }

    fun presentPaymentDetailFlow(
        session: AirwallexSession,
        paymentMethod: PaymentMethod,
        paymentConsentId: String?,
        cvc: String?
    ): LiveData<PaymentDetailResult> {
        val resultData = MutableLiveData<PaymentDetailResult>()
        airwallex.presentPaymentDetailFlow(
            session,
            paymentMethod,
            paymentConsentId,
            cvc,
            object : Airwallex.PaymentIntentCardListener {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    resultData.value = PaymentDetailResult.Success(paymentIntent)
                }

                override fun onFailed(error: Exception) {
                    resultData.value = PaymentDetailResult.Error(error)
                }

                override fun onCancelled() {
                    resultData.value = PaymentDetailResult.Cancel
                }
            }
        )
        return resultData
    }

    sealed class PaymentMethodResult {
        data class Success(
            val paymentMethod: PaymentMethod,
            val paymentConsentId: String?,
            val cvc: String?
        ) : PaymentMethodResult()

        data class Error(val exception: Exception) : PaymentMethodResult()

        object Cancel : PaymentMethodResult()
    }

    sealed class PaymentCheckoutResult {
        data class Success(val paymentIntent: PaymentIntent) : PaymentCheckoutResult()

        data class Error(val exception: Exception) : PaymentCheckoutResult()

        data class WeChatPay(val weChat: WeChat) : PaymentCheckoutResult()

        data class Redirect(val redirectUrl: String) : PaymentCheckoutResult()
    }

    sealed class ShippingResult {
        data class Success(val shipping: Shipping) : ShippingResult()

        object Cancel : ShippingResult()
    }

    sealed class PaymentFlowResult {
        data class Success(val paymentIntent: PaymentIntent) : PaymentFlowResult()

        data class Error(val exception: Exception) : PaymentFlowResult()

        data class WeChatPay(val weChat: WeChat) : PaymentFlowResult()

        data class Redirect(val redirectUrl: String) : PaymentFlowResult()

        object Cancel : PaymentFlowResult()
    }

    sealed class PaymentDetailResult {
        data class Success(val paymentIntent: PaymentIntent) : PaymentDetailResult()

        data class Error(val exception: Exception) : PaymentDetailResult()

        object Cancel : PaymentDetailResult()
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PaymentCartViewModel(
                application, airwallex
            ) as T
        }
    }
}
