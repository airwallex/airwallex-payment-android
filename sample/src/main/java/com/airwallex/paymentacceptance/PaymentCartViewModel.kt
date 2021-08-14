package com.airwallex.paymentacceptance

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.ClientSecretProvider
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.PaymentIntent
import com.airwallex.android.core.model.Shipping
import com.airwallex.android.core.model.WeChat

internal class PaymentCartViewModel(
    application: Application
) : AndroidViewModel(application) {

    fun presentShippingFlow(fragment: Fragment, shipping: Shipping?): LiveData<ShippingResult> {
        val resultData = MutableLiveData<ShippingResult>()
        AirwallexStarter.presentShippingFlow(
            fragment,
            shipping,
            object : Airwallex.PaymentShippingListener {
                override fun onSuccess(shipping: Shipping) {
                    resultData.value = ShippingResult.Success(shipping)
                }

                override fun onCancelled() {
                    resultData.value = ShippingResult.Cancel
                }

                override fun onFailed(error: AirwallexException) {
                    resultData.value = ShippingResult.Error(error)
                }
            }
        )
        return resultData
    }

    fun presentPaymentFlow(
        fragment: Fragment,
        session: AirwallexSession,
        clientSecretProvider: ClientSecretProvider? = null
    ): LiveData<PaymentFlowResult> {
        val resultData = MutableLiveData<PaymentFlowResult>()
        AirwallexStarter.presentPaymentFlow(
            fragment,
            session,
            clientSecretProvider,
            object : Airwallex.PaymentIntentListener {
                override fun onSuccess(paymentIntent: PaymentIntent) {
                    resultData.value = PaymentFlowResult.Success(paymentIntent)
                }

                override fun onFailed(error: AirwallexException) {
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

        data class Error(val exception: Exception) : ShippingResult()
    }

    sealed class PaymentFlowResult {
        data class Success(val paymentIntent: PaymentIntent) : PaymentFlowResult()

        data class Error(val exception: Exception) : PaymentFlowResult()

        data class WeChatPay(val weChat: WeChat) : PaymentFlowResult()

        data class Redirect(val redirectUrl: String) : PaymentFlowResult()

        object Cancel : PaymentFlowResult()
    }

    internal class Factory(
        private val application: Application
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return PaymentCartViewModel(
                application
            ) as T
        }
    }
}
