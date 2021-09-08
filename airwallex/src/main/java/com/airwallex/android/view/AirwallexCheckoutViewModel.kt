package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexSession
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.PPROAdditionalInfo
import com.airwallex.android.core.model.PaymentMethod

class AirwallexCheckoutViewModel(
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
            object : Airwallex.PaymentListener<String> {
                override fun onFailed(exception: AirwallexException) {
                    resultData.value = PaymentResult.Error(exception)
                }

                override fun onSuccess(response: String) {
                    resultData.value = PaymentResult.Success(response)
                }
            }
        )
        return resultData
    }

    sealed class PaymentResult {
        data class Success(val paymentIntentId: String) : PaymentResult()

        data class Error(val exception: AirwallexException) : PaymentResult()
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
