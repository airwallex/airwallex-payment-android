package com.airwallex.android.card.view

import android.app.Application
import androidx.lifecycle.*
import com.airwallex.android.core.Airwallex
import com.airwallex.android.core.AirwallexPaymentStatus
import com.airwallex.android.core.model.ContinuePaymentIntentParams

internal class DccViewModel(
    application: Application,
    private val airwallex: Airwallex
) : AndroidViewModel(application) {

    fun continuePaymentIntent(
        params: ContinuePaymentIntentParams
    ): LiveData<Result<String>> {
        val resultData = MutableLiveData<Result<String>>()

        airwallex.continueDccPaymentIntent(
            params,
            object : Airwallex.PaymentResultListener {

                override fun onCompleted(status: AirwallexPaymentStatus) {
                    when (status) {
                        is AirwallexPaymentStatus.Success -> {
                            resultData.value = Result.success(status.paymentIntentId)
                        }
                        is AirwallexPaymentStatus.Failure -> {
                            resultData.value = Result.failure(status.exception)
                        }
                        else -> Unit
                    }
                }
            }
        )
        return resultData
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
