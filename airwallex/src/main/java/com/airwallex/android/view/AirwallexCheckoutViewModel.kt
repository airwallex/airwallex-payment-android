package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.Airwallex
import com.airwallex.android.AirwallexSession
import com.airwallex.android.model.*

internal class AirwallexCheckoutViewModel(
    application: Application,
    private val airwallex: Airwallex,
    private val session: AirwallexSession
) : AndroidViewModel(application) {

    fun checkout(
        paymentMethod: PaymentMethod,
        paymentConsentId: String?,
        cvc: String?,
        pproAdditionalInfo: PPROAdditionalInfo? = null
    ): LiveData<PaymentResult> {
        val resultData = MutableLiveData<PaymentResult>()
        airwallex.checkout(
            session,
            paymentMethod,
            paymentConsentId,
            cvc,
            pproAdditionalInfo,
            object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onFailed(exception: Exception) {
                    resultData.value = PaymentResult.Error(exception)
                }

                override fun onSuccess(response: PaymentIntent) {
                    resultData.value = PaymentResult.Success(response)
                }

                override fun onNextActionWithWeChatPay(weChat: WeChat) {
                    resultData.value = PaymentResult.WeChatPay(weChat)
                }

                override fun onNextActionWithRedirectUrl(url: String) {
                    resultData.value = PaymentResult.Redirect(url)
                }
            }
        )
        return resultData
    }

    sealed class PaymentResult {
        data class Success(val paymentIntent: PaymentIntent) : PaymentResult()

        data class Error(val exception: Exception) : PaymentResult()

        data class WeChatPay(val weChat: WeChat) : PaymentResult()

        data class Redirect(val redirectUrl: String) : PaymentResult()
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex,
        private val session: AirwallexSession
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return AirwallexCheckoutViewModel(
                application, airwallex, session
            ) as T
        }
    }
}
