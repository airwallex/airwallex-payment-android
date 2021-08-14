package com.airwallex.android.card.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.exception.AirwallexException
import com.airwallex.android.core.model.ContinuePaymentIntentParams
import com.airwallex.android.core.model.PaymentIntent

internal class DccViewModel(
    application: Application,
    private val airwallex: Airwallex
) : AndroidViewModel(application) {

    fun continuePaymentIntent(
        params: ContinuePaymentIntentParams
    ): LiveData<PaymentIntentResult> {
        val resultData = MutableLiveData<PaymentIntentResult>()

        airwallex.continueDccPaymentIntent(
            params,
            object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onFailed(exception: AirwallexException) {
                    resultData.value = PaymentIntentResult.Error(exception)
                }

                override fun onSuccess(response: PaymentIntent) {
                    resultData.value = PaymentIntentResult.Success(response)
                }
            }
        )
        return resultData
    }

    sealed class PaymentIntentResult {
        data class Success(val paymentIntent: PaymentIntent) : PaymentIntentResult()

        data class Error(val exception: AirwallexException) : PaymentIntentResult()
    }

    internal class Factory(
        private val application: Application,
        private val airwallex: Airwallex
    ) : ViewModelProvider.Factory {
        override fun <T : ViewModel?> create(modelClass: Class<T>): T {
            @Suppress("UNCHECKED_CAST")
            return DccViewModel(
                application, airwallex
            ) as T
        }
    }
}
