package com.airwallex.paymentacceptance

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.*
import com.airwallex.android.model.*

internal class PaymentCartViewModel(
    application: Application,
    private val airwallex: Airwallex
) : AndroidViewModel(application) {

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
