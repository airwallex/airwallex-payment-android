package com.airwallex.android.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.Airwallex
import com.airwallex.android.model.*

internal class DccViewModel(
    application: Application,
    private val airwallex: Airwallex
) : AndroidViewModel(application) {

    fun continuePaymentIntent(
        threeDSecureActivityLaunch: ThreeDSecureActivityLaunch,
        params: ContinuePaymentIntentParams
    ): LiveData<PaymentIntentResult> {
        val resultData = MutableLiveData<PaymentIntentResult>()

        airwallex.continuePaymentIntent(
            getApplication(), threeDSecureActivityLaunch, params,
            object : Airwallex.PaymentListener<PaymentIntent> {
                override fun onFailed(exception: Exception) {
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

        data class Error(val exception: Exception) : PaymentIntentResult()
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
