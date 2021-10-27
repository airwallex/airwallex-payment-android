package com.airwallex.paymentacceptance

import android.app.Application
import androidx.fragment.app.Fragment
import androidx.lifecycle.*
import com.airwallex.android.AirwallexStarter
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.Shipping

internal class PaymentCartViewModel(
    application: Application
) : AndroidViewModel(application) {

    fun presentShippingFlow(fragment: Fragment, shipping: Shipping?): LiveData<ShippingResult> {
        val resultData = MutableLiveData<ShippingResult>()
        AirwallexStarter.presentShippingFlow(
            fragment,
            shipping,
            object : AirwallexStarter.ShippingFlowListener {
                override fun onSuccess(response: Shipping) {
                    resultData.value = ShippingResult.Success(response)
                }

                override fun onCancelled() {
                    resultData.value = ShippingResult.Cancel
                }
            }
        )
        return resultData
    }

    fun presentPaymentFlow(
        fragment: Fragment,
        session: AirwallexSession,
    ): LiveData<PaymentFlowResult> {
        val resultData = MutableLiveData<PaymentFlowResult>()
        AirwallexStarter.presentPaymentFlow(
            fragment,
            session,
            object : AirwallexStarter.PaymentFlowListener {
                override fun onSuccess(paymentIntentId: String, isRedirecting: Boolean) {
                    resultData.value = PaymentFlowResult.Success(paymentIntentId, isRedirecting)
                }

                override fun onFailed(exception: AirwallexException) {
                    resultData.value = PaymentFlowResult.Error(exception)
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
        data class Success(val paymentIntentId: String, val isRedirecting: Boolean) : PaymentFlowResult()

        data class Error(val exception: Exception) : PaymentFlowResult()

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
